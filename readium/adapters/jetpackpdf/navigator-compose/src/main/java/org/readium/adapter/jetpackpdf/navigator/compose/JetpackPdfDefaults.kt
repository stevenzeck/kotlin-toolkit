/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.adapter.jetpackpdf.navigator.compose

import org.readium.r2.navigator.preferences.ReadingProgression

/**
 * Default values for the PDF navigator with the Jetpack PDF adapter.
 *
 * These values will be used when no publication metadata or user preference takes precedence.
 *
 * @see JetpackPdfPreferences
 */
public data class JetpackPdfDefaults(
    val readingProgression: ReadingProgression? = null,
)
