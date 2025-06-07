package org.readium.r2.testapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.readium.r2.shared.publication.Publication
import org.readium.r2.testapp.about.AboutScreen
import org.readium.r2.testapp.bookshelf.BookshelfScreen
import org.readium.r2.testapp.catalogs.CatalogFeedScreen
import org.readium.r2.testapp.catalogs.CatalogScreen
import org.readium.r2.testapp.catalogs.PublicationScreen
import org.readium.r2.testapp.data.model.Catalog

sealed class Screen(val route: String) {

    sealed class TopLevel(
        route: String,
        val title: String,
        val icon: ImageVector
    ) : Screen(route) {
        object Bookshelf : TopLevel("bookshelf", "Bookshelf", Icons.Default.Book)
        object Catalogs : TopLevel("catalogs", "Catalogs", Icons.Default.Explore)
        object About : TopLevel("about", "About", Icons.Default.Info)
    }

    object CatalogDetail : Screen("catalog_detail")
    object Publication : Screen("publication")
}

private val topLevelScreens = listOf(
    Screen.TopLevel.Bookshelf,
    Screen.TopLevel.Catalogs,
    Screen.TopLevel.About,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestApp() {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Readium") })
        },
        bottomBar = {
            NavigationBar {
                topLevelScreens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
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
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.TopLevel.Bookshelf.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.TopLevel.Bookshelf.route) { BookshelfScreen() }
            composable(Screen.TopLevel.About.route) { AboutScreen() }

            composable(Screen.TopLevel.Catalogs.route) {
                CatalogFeedScreen(navController = navController)
            }

            composable(Screen.CatalogDetail.route) {
                val catalog = navController.previousBackStackEntry
                    ?.savedStateHandle?.get<Catalog>("catalog")

                if (catalog != null) {
                    CatalogScreen(
                        catalog = catalog,
                        navController = navController,
                        onFacetClick = { /* TODO */ },
                    )
                }
            }

            composable(Screen.Publication.route) {
                val publication = navController.previousBackStackEntry
                    ?.savedStateHandle?.get<Publication>("publication")

                if (publication != null) {
                    PublicationScreen(publication = publication)
                }
            }
        }
    }
}
