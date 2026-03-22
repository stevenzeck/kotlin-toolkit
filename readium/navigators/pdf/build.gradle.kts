plugins {
    id("readium.library-conventions")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "org.readium.navigator.pdf"

    buildFeatures {
        compose = true
    }
}

dependencies {
    api(project(":readium:readium-shared"))
    api(project(":readium:readium-navigator"))
    api(project(":readium:navigators:readium-navigator-common"))

    api(libs.androidx.compose.foundation)
    api(libs.kotlinx.collections.immutable)

    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.timber)
}
