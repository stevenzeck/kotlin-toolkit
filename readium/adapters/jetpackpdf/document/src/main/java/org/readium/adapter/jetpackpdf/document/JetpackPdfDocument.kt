/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

@file:OptIn(InternalReadiumApi::class)

package org.readium.adapter.jetpackpdf.document

import android.content.Context
import android.graphics.Bitmap
import android.util.Size
import androidx.pdf.PdfDocument as _JetpackPdfDocument
import androidx.pdf.PdfLoader
import androidx.pdf.PdfPasswordException
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
 * Jetpack PDF implementation of [PdfDocument].
 */
public class JetpackPdfDocument(
    public val document: _JetpackPdfDocument,
    override val identifier: String?,
) : PdfDocument {

    override val pageCount: Int
        get() = document.pageCount

    override val title: String? = null
    override val author: String? = null
    override val subject: String? = null
    override val keywords: List<String> = emptyList()
    override val outline: List<PdfDocument.OutlineNode> by lazy {
        emptyList()
    }

    override suspend fun cover(context: Context): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val pageInfo = document.getPageInfo(0)
            document.getPageBitmapSource(0).use { source ->
                source.getBitmap(
                    scaledPageSizePx = Size(pageInfo.width, pageInfo.height), tileRegion = null
                )
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
        document.close()
    }
}

public class JetpackPdfDocumentFactory(
    private val pdfLoader: PdfLoader
) : PdfDocumentFactory<JetpackPdfDocument> {

    override val documentType: KClass<JetpackPdfDocument> = JetpackPdfDocument::class

    override suspend fun open(resource: Resource, password: String?): ReadTry<JetpackPdfDocument> {
        val uri = resource.sourceUrl?.toUri()
            ?: return Try.failure(ReadError.Decoding("Jetpack PDF requires a file Uri"))

        return try {
            val jetpackDoc = pdfLoader.openDocument(uri, password)

            val identifier = uri.toString().toByteArray().md5()

            Try.success(JetpackPdfDocument(jetpackDoc, identifier))
        } catch (e: PdfPasswordException) {
            Try.failure(ReadError.Decoding(e))
        } catch (e: Exception) {
            Try.failure(ReadError.Decoding(e))
        }
    }
}
