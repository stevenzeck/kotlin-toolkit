/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.adapter.jetpackpdf.navigator

import org.readium.r2.navigator.preferences.PreferencesFilter

/**
 * Suggested filter to keep only shared [JetpackPdfPreferences].
 */
public object JetpackPdfSharedPreferencesFilter : PreferencesFilter<JetpackPdfPreferences> {

    override fun filter(preferences: JetpackPdfPreferences): JetpackPdfPreferences =
        preferences.copy(
            readingProgression = null
        )
}

/**
 * Suggested filter to keep only publication-specific [JetpackPdfPreferences].
 */
public object JetpackPdfPublicationPreferencesFilter : PreferencesFilter<JetpackPdfPreferences> {

    override fun filter(preferences: JetpackPdfPreferences): JetpackPdfPreferences =
        JetpackPdfPreferences(
            readingProgression = preferences.readingProgression
        )
}
