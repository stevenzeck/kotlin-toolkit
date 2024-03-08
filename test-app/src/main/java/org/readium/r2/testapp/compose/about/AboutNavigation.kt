package org.readium.r2.testapp.compose.about

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.readium.r2.testapp.compose.Screen

fun NavGraphBuilder.aboutScreen() {
    composable(route = Screen.BottomNav.About.route) {
        AboutScreen()
    }
}
