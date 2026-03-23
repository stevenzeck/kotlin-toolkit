/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

@file:OptIn(InternalReadiumApi::class)

package org.readium.adapter.jetpackpdf.navigator.fragment

import android.content.Context
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.ParcelFileDescriptor
import android.os.ProxyFileDescriptorCallback
import android.os.storage.StorageManager
import android.system.ErrnoException
import android.system.OsConstants
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.pdf.SandboxedPdfLoader
import androidx.pdf.view.PdfView
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.readium.r2.navigator.pdf.PdfDocumentFragment
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.InternalReadiumApi
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.Url
import org.readium.r2.shared.util.data.ReadError
import org.readium.r2.shared.util.file.FileSystemError
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.toUri
import timber.log.Timber

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalReadiumApi
@InternalReadiumApi
public class JetpackPdfDocumentFragment internal constructor(
    private val publication: Publication,
    private val href: Url,
    initialPageIndex: Int,
    initialSettings: JetpackPdfSettings,
    private val listener: Listener?,
) : PdfDocumentFragment<JetpackPdfSettings>() {

    internal interface Listener {
        fun onResourceLoadFailed(href: Url, error: ReadError)
        fun onTap(point: PointF): Boolean
    }

    private val _pageIndex = MutableStateFlow(initialPageIndex)
    override val pageIndex: StateFlow<Int> = _pageIndex.asStateFlow()

    private var settings: JetpackPdfSettings = initialSettings

    private var pdfView: PdfView? = null
    private var pdfDocument: androidx.pdf.PdfDocument? = null
    private var fileDescriptor: ParcelFileDescriptor? = null
    private var handlerThread: HandlerThread? = null

    private lateinit var gestureDetector: GestureDetector

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val context = inflater.context

        // Setup Gesture Detector for Taps
        gestureDetector = GestureDetector(
            context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    return listener?.onTap(PointF(e.x, e.y)) ?: false
                }
            }
        )

        // Create Root Layout (FrameLayout)
        val root = object : FrameLayout(context) {
            override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
                if (ev != null) {
                    gestureDetector.onTouchEvent(ev)
                }
                return super.dispatchTouchEvent(ev)
            }
        }.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Create PdfView
        pdfView = PdfView(context).apply {
            id = View.generateViewId()
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // Listen for page changes to update pageIndex
            addOnViewportChangedListener(object : PdfView.OnViewportChangedListener {
                override fun onViewportChanged(
                    firstVisiblePage: Int,
                    visiblePagesCount: Int,
                    pageLocations: android.util.SparseArray<android.graphics.RectF>,
                    zoomLevel: Float
                ) {
                    // Update the state flow with the first visible page
                    _pageIndex.value = firstVisiblePage
                }
            })
        }
        root.addView(pdfView)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDocument()
    }

    private fun loadDocument() {
        val context = requireContext()
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager

        // Create a background handler for the ProxyFileDescriptor
        handlerThread = HandlerThread("JetpackPdfProxy").apply { start() }
        val handler = Handler(handlerThread!!.looper)

        val resource = publication.get(href)
        if (resource == null) {
            listener?.onResourceLoadFailed(
                href = href,
                error = ReadError.Access(cause = FileSystemError.FileNotFound(cause = null))
            )
            return
        }

        val callback = object : ProxyFileDescriptorCallback() {
            override fun onGetSize(): Long {
                return runBlocking {
                    resource.length().getOrElse {
                        throw ErrnoException("Failed to get size", OsConstants.EIO)
                    }
                }
            }

            override fun onRead(offset: Long, size: Int, data: ByteArray): Int {
                return runBlocking {
                    // Clamp size to remaining
                    val length = resource.length().getOrElse { return@runBlocking -1 }
                    if (offset >= length) return@runBlocking 0

                    val readSize = size.coerceAtMost((length - offset).toInt())
                    val range = offset until (offset + readSize)

                    resource.read(range).map { bytes ->
                        bytes.copyInto(data)
                        bytes.size
                    }.getOrElse {
                        throw ErrnoException("Failed to read", OsConstants.EIO)
                    }
                }
            }

            override fun onRelease() {
                resource.close()
            }
        }

        // Open PFD
        try {
            fileDescriptor = storageManager.openProxyFileDescriptor(
                ParcelFileDescriptor.MODE_READ_ONLY,
                callback,
                handler
            )
        } catch (e: Exception) {
            listener?.onResourceLoadFailed(
                href = href,
                error = ReadError.Access(cause = FileSystemError.IO(e))
            )
            resource.close()
            return
        }

        val fd = fileDescriptor ?: return
        val uri = href.toUri()

        // Load Document via SandboxedPdfLoader
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val loader = SandboxedPdfLoader(context = context)
                val document = loader.openDocument(uri = uri, fileDescriptor = fd, password = null)

                withContext(Dispatchers.Main) {
                    this@JetpackPdfDocumentFragment.pdfDocument = document
                    pdfView?.pdfDocument = document
                    // Restore page index if needed
                    pdfView?.scrollToPage(pageNum = _pageIndex.value)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    listener?.onResourceLoadFailed(href = href, error = ReadError.Decoding(e))
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        try {
            pdfDocument?.close()
        } catch (e: Exception) {
            Timber.e(e)
        }
        pdfDocument = null

        try {
            fileDescriptor?.close()
        } catch (e: IOException) {
            Timber.e(e)
        }
        fileDescriptor = null

        handlerThread?.quitSafely()
        handlerThread = null

        pdfView = null
    }

    override fun goToPageIndex(index: Int, animated: Boolean): Boolean {
        if (_pageIndex.value == index) return false
        _pageIndex.value = index
        pdfView?.scrollToPage(pageNum = index)
        return true
    }

    override fun applySettings(settings: JetpackPdfSettings) {
        if (this.settings == settings) return
        this.settings = settings
    }
}
