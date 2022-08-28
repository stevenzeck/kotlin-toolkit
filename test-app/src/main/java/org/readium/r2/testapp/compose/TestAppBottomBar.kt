package org.readium.r2.testapp.compose

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import org.readium.r2.testapp.R


@ExperimentalMaterial3Api
@Composable
fun TestAppBottomBar(appState: TestAppState, tabs: Array<BottomNavTabs>) {

    val navController = appState.navController
    val currentRoute = appState.currentRoute.value?.destination?.route
    val routes = remember { BottomNavTabs.values().map { it.route } }

    // TODO is this good or bad?
    if (currentRoute in routes || currentRoute?.substringBefore("/") in routes) {
        NavigationBar {
            tabs.forEach { tab ->
                NavigationBarItem(
                    icon = { Icon(painterResource(tab.icon), contentDescription = null) },
                    label = { Text(stringResource(tab.title)) },
                    selected = (currentRoute == tab.route || currentRoute?.substringBefore("/") == tab.route),
                    onClick = {
                        if (tab.route != currentRoute) {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    alwaysShowLabel = true,
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        }
    }
}

enum class BottomNavTabs(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val route: String
) {
    BOOKSHELF(
        R.string.title_bookshelf,
        R.drawable.baseline_local_library_24,
        TestAppDestinations.BOOKSHELF
    ),
    CATALOG(
        R.string.title_catalogs,
        R.drawable.baseline_dashboard_24,
        TestAppDestinations.CATALOG
    ),
    ABOUT(R.string.title_about, R.drawable.baseline_info_24, TestAppDestinations.ABOUT)
}