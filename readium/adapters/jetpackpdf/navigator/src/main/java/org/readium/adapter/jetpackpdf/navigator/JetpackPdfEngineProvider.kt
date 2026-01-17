/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

@file:OptIn(InternalReadiumApi::class)

package org.readium.adapter.jetpackpdf.navigator

import android.graphics.PointF
import android.net.Uri
import android.os.Build
import android.os.ext.SdkExtensions
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresExtension
import org.readium.r2.navigator.OverflowableNavigator
import org.readium.r2.navigator.SimpleOverflow
import org.readium.r2.navigator.input.TapEvent
import org.readium.r2.navigator.pdf.PdfDocumentFragmentInput
import org.readium.r2.navigator.pdf.PdfEngineProvider
import org.readium.r2.navigator.preferences.Axis
import org.readium.r2.navigator.util.SingleFragmentFactory
import org.readium.r2.navigator.util.createFragmentFactory
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.InternalReadiumApi
import org.readium.r2.shared.publication.Metadata
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.Url
import org.readium.r2.shared.util.data.ReadError

@ExperimentalReadiumApi
public class JetpackPdfEngineProvider
@RequiresExtension(extension = Build.VERSION_CODES.S, version = 13) constructor(
    private val defaults: JetpackPdfDefaults = JetpackPdfDefaults(),
    private val dataSource: Uri? = null
) : PdfEngineProvider<JetpackPdfSettings, JetpackPdfPreferences, JetpackPdfPreferencesEditor> {

    public companion object {
        @ChecksSdkIntAtLeast(extension = Build.VERSION_CODES.S, api = 13)
        public fun isSupported(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                SdkExtensions.getExtensionVersion(Build.VERSION_CODES.S) >= 13
        }
    }

    init {
        require(isSupported()) {
            "JetpackPdfEngineProvider requires Android 12 (API 31) with SDK Extension 13 or higher."
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
    override fun createDocumentFragmentFactory(
        input: PdfDocumentFragmentInput<JetpackPdfSettings>
    ): SingleFragmentFactory<out JetpackPdfDocumentFragment> =
        createFragmentFactory {
            val documentHref = if (dataSource != null) {
                Url(dataSource.toString()) ?: input.href
            } else {
                input.href
            }
            JetpackPdfDocumentFragment(
                href = documentHref,
                initialPageIndex = input.pageIndex,
                initialSettings = input.settings,
                listener = object : JetpackPdfDocumentFragment.Listener {
                    override fun onResourceLoadFailed(href: Url, error: ReadError) {
                        input.navigatorListener?.onResourceLoadFailed(href, error)
                    }

                    override fun onTap(point: PointF): Boolean =
                        input.inputListener?.onTap(TapEvent(point)) ?: false
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

    override fun computeOverflow(settings: JetpackPdfSettings): OverflowableNavigator.Overflow {
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
