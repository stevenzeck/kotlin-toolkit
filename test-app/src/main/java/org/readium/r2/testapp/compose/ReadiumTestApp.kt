package org.readium.r2.testapp.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import org.readium.r2.testapp.compose.about.aboutScreen
import org.readium.r2.testapp.compose.bookshelf.bookshelfScreen
import org.readium.r2.testapp.compose.catalogs.catalogListScreen
import org.readium.r2.testapp.compose.catalogs.catalogScreen
import org.readium.r2.testapp.compose.catalogs.navigateToCatalog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadiumTestApp() {

    TestAppTheme {

        val appState = rememberTestAppState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {

                    },
                    navigationIcon = {

                    },
                    actions = {

                    }
                )
            },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {

                    },
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "TODO"
                    )
                }

            },
            bottomBar = {
                TestAppBottomBar(
                    appState.navController
                )
            }
        ) {
            NavHost(
                navController = appState.navController,
                startDestination = Screen.BottomNav.Bookshelf.route,
                modifier = Modifier.padding(it)
            ) {
                bookshelfScreen(
                    onOpenBook = { bookId ->

                    }
                )

                catalogListScreen(
                    onCatalogSelected = { catalogId ->
                        appState.navController.navigateToCatalog(catalogId)
                    }
                )

                catalogScreen(
                    onPublicationSelected = {

                    }
                )

                aboutScreen()
            }
        }
    }
}
