package org.readium.r2.testapp.compose

import android.content.res.Resources
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.readium.r2.testapp.compose.TestAppDestinations.ABOUT
import org.readium.r2.testapp.compose.TestAppDestinations.BOOKSHELF
import org.readium.r2.testapp.compose.TestAppDestinations.CATALOG

@ExperimentalMaterial3Api
class TestAppState(
    val navController: NavHostController,
    private val resources: Resources,
) {
    var topBarState: TopBarState by mutableStateOf(TopBarState())

    fun upPress() {
        navController.navigateUp()
    }

    val currentRoute: State<NavBackStackEntry?>
        @Composable get() = navController
            .currentBackStackEntryAsState()

    val showBackButton: Boolean
        @Composable get() = !arrayOf(
            BOOKSHELF,
            CATALOG,
            ABOUT
        ).contains(currentRoute.value?.destination?.route)

    val showFab: Boolean
        @Composable get() = arrayOf(
            BOOKSHELF,
            CATALOG,
        ).contains(currentRoute.value?.destination?.route)
}

@ExperimentalMaterial3Api
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