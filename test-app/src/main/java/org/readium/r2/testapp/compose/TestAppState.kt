package org.readium.r2.testapp.compose

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

class TestAppState(
    val navController: NavHostController,
    private val resources: Resources,
) {
    fun upPress() {
        navController.navigateUp()
    }

    private val currentRoute: State<NavBackStackEntry?>
        @Composable get() = navController
            .currentBackStackEntryAsState()

    val showBackButton: Boolean
        @Composable get() = !arrayOf(
            Screen.BottomNav.Bookshelf.route,
            Screen.BottomNav.Catalogs.route,
            Screen.BottomNav.About.route,
        ).contains(currentRoute.value?.destination?.route)
}

@Composable
fun rememberTestAppState(
    navController: NavHostController = rememberNavController(),
    resources: Resources = resources(),
) = remember(navController, resources) {
    TestAppState(navController, resources)
}

@Composable
@ReadOnlyComposable
private fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}