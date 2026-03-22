/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

@file:OptIn(InternalReadiumApi::class)

package org.readium.navigator.pdf

import org.readium.navigator.common.Preferences
import org.readium.navigator.common.PreferencesEditor
import org.readium.navigator.common.Settings
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.InternalReadiumApi
import org.readium.r2.shared.publication.Publication

/**
 * Factory of the PDF navigator and related components.
 *
 * @param publication PDF publication to render in the navigator.
 * @param pdfEngineProvider provider for third-party PDF engine adapter.
 */
@ExperimentalReadiumApi
public class PdfNavigatorFactory<S : Settings, P : Preferences<P>, E : PreferencesEditor<P, S>>(
    private val publication: Publication,
    private val pdfEngineProvider: PdfEngineProvider<S, P, E>,
) {

    /**
     * Creates a preferences editor for [publication] with [initialPreferences].
     *
     * @param initialPreferences Initial set of preferences for the editor.
     */
    public fun createPreferencesEditor(
        initialPreferences: P,
    ): E =
        pdfEngineProvider.createPreferenceEditor(
            publication = publication,
            initialPreferences = initialPreferences
        )
}
