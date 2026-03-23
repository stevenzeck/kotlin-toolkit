/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.adapter.jetpackpdf.navigator.compose

import kotlinx.serialization.Serializable
import org.readium.navigator.common.Preferences
import org.readium.r2.navigator.preferences.ReadingProgression
import org.readium.r2.shared.ExperimentalReadiumApi

/**
 * User preferences for the Jetpack PDF engine.
 */
@OptIn(ExperimentalReadiumApi::class)
@Serializable
public data class JetpackPdfPreferences(
    val readingProgression: ReadingProgression? = null,
) : Preferences<JetpackPdfPreferences> {

    override fun plus(other: JetpackPdfPreferences): JetpackPdfPreferences =
        copy(
            readingProgression = other.readingProgression ?: readingProgression,
        )
}
