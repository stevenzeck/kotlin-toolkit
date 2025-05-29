/*
 * Copyright 2022 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

plugins {
    id("readium.library-conventions")
    `dokka-convention`
}

android {
    namespace = "org.readium.adapter.pdfium.document"
}

dependencies {
    api(project(":readium:readium-shared"))
    implementation(project(":readium:adapters:pdfium:readium-adapter-pdfium-common"))

    implementation(libs.timber)
    implementation(libs.kotlinx.coroutines.android)
}
