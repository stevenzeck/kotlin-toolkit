/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.adapter.jetpackpdf.navigator.compose

import org.readium.navigator.common.Settings
import org.readium.r2.navigator.preferences.ReadingProgression
import org.readium.r2.shared.ExperimentalReadiumApi

/**
 * Settings for the Jetpack PDF engine.
 */
@OptIn(ExperimentalReadiumApi::class)
public data class JetpackPdfSettings(
    val readingProgression: ReadingProgression,
) : Settings
