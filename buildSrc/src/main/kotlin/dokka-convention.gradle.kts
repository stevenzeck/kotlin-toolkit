plugins {
    id("org.jetbrains.dokka")
}

dokka {
    dokkaSourceSets.configureEach {
        reportUndocumented = false
        skipEmptyPackages = false
        skipDeprecated = true
    }
}
