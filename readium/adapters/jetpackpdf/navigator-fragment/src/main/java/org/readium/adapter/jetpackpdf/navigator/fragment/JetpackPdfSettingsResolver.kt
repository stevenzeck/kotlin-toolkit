/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.adapter.jetpackpdf.navigator.fragment

import org.readium.r2.navigator.preferences.ReadingProgression
import org.readium.r2.shared.publication.Metadata
import org.readium.r2.shared.publication.ReadingProgression as PublicationReadingProgression

internal class JetpackPdfSettingsResolver(
    private val metadata: Metadata,
    private val defaults: JetpackPdfDefaults,
) {

    fun settings(preferences: JetpackPdfPreferences): JetpackPdfSettings {

        val readingProgression: ReadingProgression =
            preferences.readingProgression ?: when (metadata.readingProgression) {
                PublicationReadingProgression.LTR -> ReadingProgression.LTR
                PublicationReadingProgression.RTL -> ReadingProgression.RTL
                else -> null
            } ?: defaults.readingProgression ?: ReadingProgression.LTR

        return JetpackPdfSettings(
            readingProgression = readingProgression,
        )
    }
}
