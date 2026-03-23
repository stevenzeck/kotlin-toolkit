/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.adapter.jetpackpdf.navigator.fragment

import kotlinx.serialization.Serializable
import org.readium.r2.navigator.preferences.Configurable
import org.readium.r2.navigator.preferences.ReadingProgression

/**
 * User preferences for the Jetpack PDF engine.
 */
@Serializable
public data class JetpackPdfPreferences(
    val readingProgression: ReadingProgression? = null,
) : Configurable.Preferences<JetpackPdfPreferences> {

    override fun plus(other: JetpackPdfPreferences): JetpackPdfPreferences =
        copy(
            readingProgression = other.readingProgression ?: readingProgression,
        )
}
