/*
 * Copyright 2022 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

plugins {
    id("readium.library-conventions")
    alias(libs.plugins.kotlin.serialization)
}

android {
    buildFeatures {
        viewBinding = true
    }
    namespace = "org.readium.adapter.jetpackpdf.navigator"
}

dependencies {
    api(project(":readium:readium-shared"))
    api(project(":readium:readium-navigator"))
    api(project(":readium:adapters:jetpackpdf:readium-adapter-jetpackpdf-document"))

    implementation(libs.androidx.fragment.ktx)
    implementation(libs.timber)
    implementation(libs.androidx.pdf.document)
    implementation(libs.androidx.pdf.viewer)
    implementation(libs.androidx.pdf.viewer.fragment)
    implementation(libs.androidx.pdf.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
}
