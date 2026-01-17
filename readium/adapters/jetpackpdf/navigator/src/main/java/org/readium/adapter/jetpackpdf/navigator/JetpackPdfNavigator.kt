/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.adapter.jetpackpdf.navigator

import org.readium.r2.navigator.pdf.PdfNavigatorFactory
import org.readium.r2.navigator.pdf.PdfNavigatorFragment
import org.readium.r2.shared.ExperimentalReadiumApi

@ExperimentalReadiumApi
public typealias JetpackPdfNavigatorFragment = PdfNavigatorFragment<JetpackPdfSettings, JetpackPdfPreferences>

@ExperimentalReadiumApi
public typealias JetpackPdfNavigatorFactory = PdfNavigatorFactory<JetpackPdfSettings, JetpackPdfPreferences, JetpackPdfPreferencesEditor>
