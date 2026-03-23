/*
 * Copyright 2026 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

plugins {
    id("readium.library-conventions")
}

android {
    namespace = "org.readium.adapter.jetpackpdf"
}

dependencies {
    api(project(":readium:adapters:jetpackpdf:readium-adapter-jetpackpdf-document"))
    api(project(":readium:adapters:jetpackpdf:readium-adapter-jetpackpdf-navigator-fragment"))
    api(project(":readium:adapters:jetpackpdf:readium-adapter-jetpackpdf-navigator-compose"))
}
