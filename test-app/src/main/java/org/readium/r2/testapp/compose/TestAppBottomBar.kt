package org.readium.r2.testapp.compose

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.readium.r2.testapp.R

@Composable
fun TestAppBottomBar(navController: NavHostController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val items =
            listOf(Screen.BottomNav.Bookshelf, Screen.BottomNav.Catalogs, Screen.BottomNav.About)
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(stringResource(screen.title!!)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

sealed class Screen(val route: String, @StringRes val title: Int? = null) {
    object CatalogDetail : Screen("catalog")
    object PublicationDetail : Screen("publication")

    sealed class BottomNav(route: String, @StringRes title: Int, val icon: ImageVector) :
        Screen(route, title) {
        object Bookshelf :
            BottomNav("bookshelf", R.string.title_bookshelf, Icons.Default.LocalLibrary)

        object Catalogs : BottomNav("catalogs", R.string.title_catalogs, Icons.Default.Dashboard)
        object About : BottomNav("about", R.string.title_about, Icons.Default.Info)
    }
}
