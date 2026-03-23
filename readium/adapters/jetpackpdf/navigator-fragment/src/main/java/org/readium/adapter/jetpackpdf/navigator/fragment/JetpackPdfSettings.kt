/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.adapter.jetpackpdf.navigator.fragment

import org.readium.r2.navigator.preferences.Configurable
import org.readium.r2.navigator.preferences.ReadingProgression

/**
 * Settings for the Jetpack PDF engine.
 */
public data class JetpackPdfSettings(
    val readingProgression: ReadingProgression,
) : Configurable.Settings
