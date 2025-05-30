/*
 * Copyright 2021 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

plugins {
    `dokka-convention`
    alias(libs.plugins.ktlint)
    alias(libs.plugins.compose.compiler) apply false
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    ktlint {
        android.set(true)
    }
}

tasks.register("cleanDocs", Delete::class).configure {
    delete("${project.rootDir}/docs/readium", "${project.rootDir}/docs/index.md", "${project.rootDir}/site")
}

dependencies {
    dokka(project(":readium:readium-streamer"))
    dokka(project(":readium:readium-shared"))
    dokka(project(":readium:readium-opds"))
    dokka(project(":readium:adapters:exoplayer:readium-adapter-exoplayer-audio"))
    dokka(project(":readium:navigators:media:readium-navigator-media-tts"))
    dokka(project(":readium:navigators:media:readium-navigator-media-audio"))
    dokka(project(":readium:navigators:media:readium-navigator-media-common"))
    dokka(project(":readium:navigators:readium-navigator-common"))
    dokka(project(":readium:readium-navigator"))
    dokka(project(":readium:readium-lcp"))
    dokka(project(":readium:adapters:pspdfkit:readium-adapter-pspdfkit-navigator"))
    dokka(project(":readium:adapters:pspdfkit:readium-adapter-pspdfkit-document"))
    dokka(project(":readium:adapters:pdfium:readium-adapter-pdfium-navigator"))
    dokka(project(":readium:adapters:pdfium:readium-adapter-pdfium-document"))
    dokka(project(":readium:adapters:pdfium:readium-adapter-pdfium-common"))

    dokkaHtmlPlugin("org.jetbrains.dokka:versioning-plugin")
}

dokka {
    dokkaPublications.html {
        outputDirectory.set(layout.projectDirectory.dir("docsnew"))
    }
    pluginsConfiguration {
        versioning{
            version = property("pom.version") as String

            olderVersionsDir = layout.projectDirectory.dir("previousDocVersions")
        }
    }
}
