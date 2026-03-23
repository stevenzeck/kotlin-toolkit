/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

@file:OptIn(InternalReadiumApi::class)

package org.readium.adapter.jetpackpdf.navigator.compose

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.ParcelFileDescriptor
import android.os.ProxyFileDescriptorCallback
import android.os.storage.StorageManager
import android.system.ErrnoException
import android.system.OsConstants
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.pdf.PdfDocument
import androidx.pdf.SandboxedPdfLoader
import androidx.pdf.compose.PdfViewer
import androidx.pdf.compose.PdfViewerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.readium.navigator.common.Overflow
import org.readium.navigator.common.SimpleOverflow
import org.readium.navigator.pdf.PdfDocumentInput
import org.readium.navigator.pdf.PdfEngineProvider
import org.readium.r2.navigator.preferences.Axis
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.InternalReadiumApi
import org.readium.r2.shared.publication.Metadata
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.toUri

@ExperimentalReadiumApi
public class JetpackPdfEngineProvider(
    private val defaults: JetpackPdfDefaults = JetpackPdfDefaults(),
) : PdfEngineProvider<JetpackPdfSettings, JetpackPdfPreferences, JetpackPdfPreferencesEditor> {

    public companion object {

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
        public fun isSupported(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        }
    }

    init {
        require(isSupported()) {
            "JetpackPdfEngineProvider requires Android 8.0 (API 26) or higher."
        }
    }

    @Composable
    override fun Document(
        input: PdfDocumentInput<JetpackPdfSettings>,
        modifier: Modifier
    ) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val pdfViewerState = remember { PdfViewerState() }

        val documentState = remember { mutableStateOf<PdfDocument?>(null) }

        LaunchedEffect(input.publication, input.href) {
            val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val handlerThread = HandlerThread("JetpackPdfProxy").apply { start() }
            val handler = Handler(handlerThread.looper)

            val resource = input.publication.get(input.href)
            if (resource == null) {
                return@LaunchedEffect
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

            val fileDescriptor = try {
                storageManager.openProxyFileDescriptor(
                    ParcelFileDescriptor.MODE_READ_ONLY,
                    callback,
                    handler
                )
            } catch (e: Exception) {
                resource.close()
                null
            }

            if (fileDescriptor != null) {
                withContext(Dispatchers.IO) {
                    try {
                        val loader = SandboxedPdfLoader(context)
                        val document = loader.openDocument(input.href.toUri(), fileDescriptor, null)
                        documentState.value = document
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            try {
                awaitCancellation()
            } finally {
                documentState.value = null
                try {
                    fileDescriptor?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                handlerThread.quitSafely()
            }
        }

        LaunchedEffect(input.pageIndex) {
            pdfViewerState.scrollToPage(input.pageIndex)
        }

        PdfViewer(
            pdfDocument = documentState.value,
            state = pdfViewerState,
            modifier = modifier,
            onFirstContentLoad = {
                coroutineScope.launch {
                    pdfViewerState.scrollToPage(input.pageIndex)
                }
            }
        )
    }

    override fun computeSettings(
        metadata: Metadata,
        preferences: JetpackPdfPreferences
    ): JetpackPdfSettings {
        val settingsPolicy = JetpackPdfSettingsResolver(metadata, defaults)
        return settingsPolicy.settings(preferences)
    }

    override fun computeOverflow(settings: JetpackPdfSettings): Overflow {
        return SimpleOverflow(
            readingProgression = settings.readingProgression,
            scroll = true,
            axis = Axis.VERTICAL
        )
    }

    override fun createPreferenceEditor(
        publication: Publication,
        initialPreferences: JetpackPdfPreferences
    ): JetpackPdfPreferencesEditor =
        JetpackPdfPreferencesEditor(initialPreferences, publication.metadata, defaults)

    override fun createEmptyPreferences(): JetpackPdfPreferences =
        JetpackPdfPreferences()
}
