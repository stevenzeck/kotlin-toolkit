package org.readium.r2.testapp.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.readium.r2.testapp.about.AboutScreen
import org.readium.r2.testapp.bookshelf.BookshelfScreen
import org.readium.r2.testapp.catalogs.CatalogDetailScreen
import org.readium.r2.testapp.compose.TestAppDestinations.CATALOG_DETAIL_KEY

object TestAppDestinations {
    const val BOOKSHELF = "bookshelf"
    const val CATALOG = "catalog"
    const val ABOUT = "about"
    const val CATALOG_DETAIL = "catalogDetail"
    const val CATALOG_DETAIL_KEY = "catalogId"
}

@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Composable
fun NavGraph(
    appState: TestAppState,
    startDestination: String = TestAppDestinations.BOOKSHELF
) {
    val actions = remember(appState.navController) { MainActions(appState.navController) }

    NavHost(
        navController = appState.navController,
        startDestination = startDestination
    ) {
        composable(BottomNavTabs.BOOKSHELF.route) {
            BookshelfScreen(
                updateTopBarState = { appState.topBarState = it },
            )
        }
        composable(BottomNavTabs.CATALOG.route) {
            CatalogListScreen(
                updateTopBarState = { appState.topBarState = it },
                onCatalogSelected = actions.navigateToCatalogDetail
            )
        }
        composable(BottomNavTabs.ABOUT.route) {
            AboutScreen(
                updateTopBarState = { appState.topBarState = it },
            )
        }
        composable("${BottomNavTabs.CATALOG.route}/${TestAppDestinations.CATALOG_DETAIL}/{$CATALOG_DETAIL_KEY}",
            arguments = listOf(
                navArgument(CATALOG_DETAIL_KEY) {
                    type = NavType.LongType
                }
            )) { backStackEntry ->
            CatalogDetailScreen(
                catalogId = backStackEntry.arguments?.getLong(CATALOG_DETAIL_KEY)
            )
        }
    }
}

class MainActions(navController: NavHostController) {
    val navigateToCatalogDetail: (Long) -> Unit = { catalogId ->
        navController.navigate("${BottomNavTabs.CATALOG.route}/${TestAppDestinations.CATALOG_DETAIL}/${catalogId}")
    }
}