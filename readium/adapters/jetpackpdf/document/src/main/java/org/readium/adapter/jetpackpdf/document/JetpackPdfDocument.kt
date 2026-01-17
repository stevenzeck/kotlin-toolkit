/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

@file:OptIn(InternalReadiumApi::class)

package org.readium.adapter.jetpackpdf.document

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import java.io.File
import kotlin.reflect.KClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.readium.r2.shared.InternalReadiumApi
import org.readium.r2.shared.extensions.md5
import org.readium.r2.shared.util.Try
import org.readium.r2.shared.util.data.ReadError
import org.readium.r2.shared.util.data.ReadTry
import org.readium.r2.shared.util.pdf.PdfDocument
import org.readium.r2.shared.util.pdf.PdfDocumentFactory
import org.readium.r2.shared.util.resource.Resource
import org.readium.r2.shared.util.toUri
import timber.log.Timber

/**
 * Implementation of [PdfDocument] using the standard Android [PdfRenderer].
 * Used for metadata parsing and cover generation.
 */
public class JetpackPdfDocument(
    public val renderer: PdfRenderer,
    public val fileDescriptor: ParcelFileDescriptor,
    override val identifier: String?,
) : PdfDocument {

    override val pageCount: Int
        get() = renderer.pageCount

    // PdfRenderer does not extract metadata like Title/Author.
    // We leave these null as per the interface default.
    override val title: String? = null
    override val author: String? = null
    override val subject: String? = null
    override val keywords: List<String> = emptyList()
    override val outline: List<PdfDocument.OutlineNode> = emptyList()

    override suspend fun cover(context: Context): Bitmap? = withContext(Dispatchers.IO) {
        try {
            if (pageCount > 0) {
                // PdfRenderer is not thread-safe, so we synchronize access
                synchronized(renderer) {
                    renderer.openPage(0).use { page ->
                        val width = page.width
                        val height = page.height
                        // Avoid creating massive bitmaps if the PDF page is huge
                        val scale = if (width > 1000) 1000f / width else 1f

                        val bitmap = Bitmap.createBitmap(
                            (width * scale).toInt(),
                            (height * scale).toInt(),
                            Bitmap.Config.ARGB_8888
                        )

                        page.render(
                            bitmap,
                            null,
                            null,
                            PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                        )
                        bitmap
                    }
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to render PDF cover")
            null
        } catch (e: OutOfMemoryError) {
            Timber.e(e, "OOM while rendering PDF cover")
            null
        }
    }

    override fun close() {
        try {
            renderer.close()
            fileDescriptor.close()
        } catch (e: Exception) {
            Timber.w(e, "Error closing JetpackPdfDocument")
        }
    }
}

public class JetpackPdfDocumentFactory(
    private val context: Context
) : PdfDocumentFactory<JetpackPdfDocument> {

    override val documentType: KClass<JetpackPdfDocument> = JetpackPdfDocument::class

    override suspend fun open(resource: Resource, password: String?): ReadTry<JetpackPdfDocument> {
        val uri = resource.sourceUrl?.toUri()
            ?: return Try.failure(ReadError.Decoding("Jetpack PDF requires a file Uri"))

        return withContext(Dispatchers.IO) {
            try {
                val pfd = try {
                    context.contentResolver.openFileDescriptor(uri, "r")
                } catch (e: Exception) {
                    if (uri.scheme == "file" && uri.path != null) {
                        ParcelFileDescriptor.open(
                            File(uri.path!!),
                            ParcelFileDescriptor.MODE_READ_ONLY
                        )
                    } else {
                        throw e
                    }
                }

                if (pfd == null) {
                    return@withContext Try.failure(ReadError.Decoding("Could not open file descriptor for $uri"))
                }

                val renderer = PdfRenderer(pfd)
                val identifier = uri.toString().toByteArray().md5()

                Try.success(JetpackPdfDocument(renderer, pfd, identifier))
            } catch (e: SecurityException) {
                Try.failure(ReadError.Decoding(e))
            } catch (e: Exception) {
                Try.failure(ReadError.Decoding(e))
            }
        }
    }
}
