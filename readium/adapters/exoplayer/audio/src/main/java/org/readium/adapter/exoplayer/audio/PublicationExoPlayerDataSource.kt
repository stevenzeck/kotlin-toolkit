/*
 * Copyright 2022 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.adapter.exoplayer.audio

import android.net.Uri
import androidx.media3.common.C.LENGTH_UNSET
import androidx.media3.common.C.RESULT_END_OF_INPUT
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener
import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.data.ReadException
import org.readium.r2.shared.util.getOrThrow
import org.readium.r2.shared.util.resource.Resource
import org.readium.r2.shared.util.resource.buffered
import org.readium.r2.shared.util.toUrl

internal sealed class ExoPlayerDataSourceException(message: String, cause: Throwable?) : IOException(
    message,
    cause
) {
    class NotOpened(message: String) : ExoPlayerDataSourceException(message, null)
    class NotFound(message: String) : ExoPlayerDataSourceException(message, null)
    class ReadFailed(uri: Uri, offset: Int, readLength: Int, cause: Throwable) : ExoPlayerDataSourceException(
        "Failed to read $readLength bytes of URI $uri at offset $offset.",
        cause
    )
}

/**
 * An ExoPlayer's [DataSource] which retrieves resources from a [Publication].
 */
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
public class PublicationExoPlayerDataSource internal constructor(
    private val publication: Publication
) : BaseDataSource(/* isNetwork = */ true) {

    public class Factory(
        private val publication: Publication,
        private val transferListener: TransferListener? = null
    ) : DataSource.Factory {

        override fun createDataSource(): DataSource =
            PublicationExoPlayerDataSource(publication).apply {
                if (transferListener != null) {
                    addTransferListener(transferListener)
                }
            }
    }

    private data class OpenedResource(
        val resource: Resource,
        val uri: Uri,
        var position: Long
    )

    private var openedResource: OpenedResource? = null

    override fun open(dataSpec: DataSpec): Long {
        val resource = dataSpec.uri.toUrl()
            ?.let { publication.linkWithHref(it) }
            ?.let { publication.get(it) }
            // Significantly improves performances, in particular with deflated ZIP entries.
            ?.buffered(resourceLength = cachedLengths[dataSpec.uri.toString()])
            ?: throw ExoPlayerDataSourceException.NotFound(
                "Can't find a [Link] for URI: ${dataSpec.uri}. Make sure you only request resources declared in the manifest."
            )

        openedResource = OpenedResource(
            resource = resource,
            uri = dataSpec.uri,
            position = dataSpec.position
        )

        val bytesToRead =
            if (dataSpec.length != LENGTH_UNSET.toLong()) {
                dataSpec.length
            } else {
                val contentLength = contentLengthOf(dataSpec.uri, resource)
                    ?: return dataSpec.length
                contentLength - dataSpec.position
            }

        return bytesToRead
    }

    /** Cached content lengths indexed by their URL. */
    private var cachedLengths: MutableMap<String, Long> = mutableMapOf()

    private fun contentLengthOf(uri: Uri, resource: Resource): Long? {
        cachedLengths[uri.toString()]?.let { return it }

        val length = runBlocking { resource.length() }.getOrNull()
            ?: return null

        cachedLengths[uri.toString()] = length
        return length
    }

    override fun read(target: ByteArray, offset: Int, length: Int): Int {
        if (length <= 0) {
            return 0
        }

        val openedResource = openedResource ?: throw ExoPlayerDataSourceException.NotOpened(
            "No opened resource to read from. Did you call open()?"
        )

        try {
            val data = runBlocking {
                openedResource.resource
                    .read(range = openedResource.position until (openedResource.position + length))
                    .mapFailure { ReadException(it) }
                    .getOrThrow()
            }

            if (data.isEmpty()) {
                return RESULT_END_OF_INPUT
            }

            data.copyInto(
                destination = target,
                destinationOffset = offset,
                startIndex = 0,
                endIndex = data.size
            )

            openedResource.position += data.count()
            return data.count()
        } catch (e: Exception) {
            if (e is InterruptedException) {
                return 0
            }
            throw ExoPlayerDataSourceException.ReadFailed(
                uri = openedResource.uri,
                offset = offset,
                readLength = length,
                cause = e
            )
        }
    }

    override fun getUri(): Uri? = openedResource?.uri

    override fun close() {
        openedResource?.run {
            try {
                runBlocking { resource.close() }
            } catch (e: Exception) {
                if (e !is InterruptedException) {
                    throw e
                }
            } finally {
                openedResource = null
            }
        }
    }
}
