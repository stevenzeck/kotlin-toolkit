/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.navigator.pdf

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.readium.navigator.common.InputListener
import org.readium.navigator.common.Overflow
import org.readium.navigator.common.Preferences
import org.readium.navigator.common.PreferencesEditor
import org.readium.navigator.common.Settings
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Metadata
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.Url

/**
 * To be implemented by adapters for third-party PDF engines which can be used with the Compose-based PdfNavigator.
 */
@ExperimentalReadiumApi
public interface PdfEngineProvider<S : Settings, P : Preferences<P>, E : PreferencesEditor<P, S>> {

    public interface Listener

    /**
     * Renders the PDF document.
     */
    @Composable
    public fun Document(
        input: PdfDocumentInput<S>,
        modifier: Modifier
    )

    /**
     * Creates settings for [metadata] and [preferences].
     */
    public fun computeSettings(metadata: Metadata, preferences: P): S

    /**
     * Infers a [Overflow] from [settings].
     */
    public fun computeOverflow(settings: S): Overflow

    /**
     * Creates a preferences editor for [publication] and [initialPreferences].
     */
    public fun createPreferenceEditor(publication: Publication, initialPreferences: P): E

    /**
     * Creates an empty set of preferences of this PDF engine provider.
     */
    public fun createEmptyPreferences(): P
}

@ExperimentalReadiumApi
public data class PdfDocumentInput<S : Settings>(
    val publication: Publication,
    val href: Url,
    val pageIndex: Int,
    val settings: S,
//    val navigatorListener: Navigator.Listener?,
    val inputListener: InputListener?,
)
