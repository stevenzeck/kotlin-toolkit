/*
 * Copyright 2022 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

plugins {
    id("readium.library-conventions")
}

android {
    namespace = "org.readium.adapter.jetpackpdf.document"
}

dependencies {
    api(project(":readium:readium-shared"))

    implementation(libs.timber)
    implementation(libs.androidx.pdf.document)
    implementation(libs.androidx.pdf.viewer)
    implementation(libs.androidx.pdf.viewer.fragment)
    implementation(libs.kotlinx.coroutines.android)
}
