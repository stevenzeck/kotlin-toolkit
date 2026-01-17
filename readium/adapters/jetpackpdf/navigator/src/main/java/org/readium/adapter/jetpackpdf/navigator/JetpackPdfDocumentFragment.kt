/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

@file:OptIn(InternalReadiumApi::class)

package org.readium.adapter.jetpackpdf.navigator

import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresExtension
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commitNow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.readium.r2.navigator.pdf.PdfDocumentFragment
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.InternalReadiumApi
import org.readium.r2.shared.util.Url
import org.readium.r2.shared.util.data.ReadError
import org.readium.r2.shared.util.toUri

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
@ExperimentalReadiumApi
public class JetpackPdfDocumentFragment internal constructor(
    private val href: Url,
    initialPageIndex: Int,
    initialSettings: JetpackPdfSettings,
    private val listener: Listener?,
) : PdfDocumentFragment<JetpackPdfSettings>() {

    internal interface Listener {
        fun onResourceLoadFailed(href: Url, error: ReadError)
        fun onTap(point: PointF): Boolean
    }

    private companion object {
        private const val VIEWER_TAG = "androidx.pdf.viewer.fragment.PdfViewerFragment"
    }

    private val _pageIndex = MutableStateFlow(initialPageIndex)
    override val pageIndex: StateFlow<Int> = _pageIndex.asStateFlow()

    private var settings: JetpackPdfSettings = initialSettings

    // Reference to the internal Jetpack fragment
    private var jetpackViewer: androidx.pdf.viewer.fragment.PdfViewerFragment? = null

    private lateinit var gestureDetector: GestureDetector

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        gestureDetector = GestureDetector(
            inflater.context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    return listener?.onTap(PointF(e.x, e.y)) ?: false
                }
            }
        )

        val root = object : FrameLayout(inflater.context) {
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

        val fragmentContainer = FragmentContainerView(inflater.context).apply {
            id = View.generateViewId()
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        root.addView(fragmentContainer)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // FIX: Always reset (recreate) the internal fragment on view creation.
        // PdfViewerFragment fails to restore its internal rendering
        // state correctly on rotation (white screen).
        reset()
    }

    override fun goToPageIndex(index: Int, animated: Boolean): Boolean {
        if (_pageIndex.value == index) return false

        _pageIndex.value = index

        // LIMITATION: androidx.pdf.viewer.fragment.PdfViewerFragment (as of Alpha)
        // does not expose a public API to scroll to a specific page programmatically.
        reset()

        return true
    }

    override fun applySettings(settings: JetpackPdfSettings) {
        if (this.settings == settings) return
        this.settings = settings
        reset()
    }

    private fun reset() {
        val containerId = (view as? ViewGroup)?.getChildAt(0)?.id ?: return
        val uri = href.toUri()

        // Note: As of androidx.pdf alpha, there is no explicit callback for load failures
        // exposed on the Fragment API. We cannot easily invoke listener.onResourceLoadFailed here yet.

        val fragment = androidx.pdf.viewer.fragment.PdfViewerFragment().apply {

            arguments = Bundle().apply {
                putParcelable("documentUri", href.toUri())
                putInt("page", _pageIndex.value)
            }
        }

        jetpackViewer = fragment

        childFragmentManager.commitNow {
            replace(containerId, fragment, VIEWER_TAG)
        }

        if (fragment.documentUri == null) {
            fragment.documentUri = uri
        }
    }
}
