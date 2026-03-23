/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.adapter.jetpackpdf.navigator.compose

import kotlinx.serialization.json.Json
import org.readium.navigator.common.PreferencesSerializer
import org.readium.r2.shared.ExperimentalReadiumApi

/**
 * JSON serializer of [JetpackPdfPreferences].
 */
@OptIn(ExperimentalReadiumApi::class)
public class JetpackPdfPreferencesSerializer : PreferencesSerializer<JetpackPdfPreferences> {

    override fun serialize(preferences: JetpackPdfPreferences): String =
        Json.encodeToString(JetpackPdfPreferences.serializer(), preferences)

    override fun deserialize(preferences: String): JetpackPdfPreferences =
        Json.decodeFromString(JetpackPdfPreferences.serializer(), preferences)
}
