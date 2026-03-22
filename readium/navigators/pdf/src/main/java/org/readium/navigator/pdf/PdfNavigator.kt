/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.navigator.pdf

import android.app.Application
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import org.readium.navigator.common.InputListener
import org.readium.navigator.common.Preferences
import org.readium.navigator.common.PreferencesEditor
import org.readium.navigator.common.Settings
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication

/**
 * Compose-based Navigator for PDF publications.
 */
@ExperimentalReadiumApi
@Composable
public fun <S : Settings, P : Preferences<P>, E : PreferencesEditor<P, S>> PdfNavigator(
    modifier: Modifier = Modifier,
    publication: Publication,
    pdfEngineProvider: PdfEngineProvider<S, P, E>,
    initialLocator: Locator? = null,
    initialPreferences: P? = null,
    inputListener: InputListener? = null,
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val viewModel: PdfNavigatorViewModel<S, P> = viewModel(
        factory = PdfNavigatorViewModel.createFactory(
            application = application,
            publication = publication,
            initialLocations = initialLocator?.locations,
            initialPreferences = initialPreferences ?: pdfEngineProvider.createEmptyPreferences(),
            pdfEngineProvider = pdfEngineProvider
        )
    )

    val currentLocator by viewModel.currentLocator.collectAsState()
    val settings by viewModel.settings.collectAsState()

    val link = publication.linkWithHref(href = currentLocator.href)
        ?: publication.readingOrder.firstOrNull()
        ?: return

    val pageIndex = currentLocator.locations.position?.let { it - 1 } ?: 0

    val input = PdfDocumentInput(
        publication = publication,
        href = link.url(),
        pageIndex = pageIndex,
        settings = settings,
//        navigatorListener = listener,
        inputListener = inputListener
    )

    pdfEngineProvider.Document(input = input, modifier = modifier.fillMaxSize())
}
