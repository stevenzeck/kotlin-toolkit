/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.adapter.jetpackpdf.navigator.compose

import org.readium.navigator.common.PreferencesEditor
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Metadata

@OptIn(ExperimentalReadiumApi::class)
public class JetpackPdfPreferencesEditor internal constructor(
    initialPreferences: JetpackPdfPreferences,
    publicationMetadata: Metadata,
    defaults: JetpackPdfDefaults,
) : PreferencesEditor<JetpackPdfPreferences, JetpackPdfSettings> {

    private data class State(
        val preferences: JetpackPdfPreferences,
        val settings: JetpackPdfSettings,
    )

    private val settingsResolver: JetpackPdfSettingsResolver =
        JetpackPdfSettingsResolver(metadata = publicationMetadata, defaults = defaults)

    private var state: State = initialPreferences.toState()

    override val preferences: JetpackPdfPreferences
        get() = state.preferences

    override val settings: JetpackPdfSettings
        get() = state.settings

    /**
     * Reset all preferences.
     */
    override fun clear() {
        updateValues { JetpackPdfPreferences() }
    }

    private fun updateValues(updater: (JetpackPdfPreferences) -> JetpackPdfPreferences) {
        val newPreferences = updater(preferences)
        state = newPreferences.toState()
    }

    private fun JetpackPdfPreferences.toState() =
        State(preferences = this, settings = settingsResolver.settings(preferences = this))
}
