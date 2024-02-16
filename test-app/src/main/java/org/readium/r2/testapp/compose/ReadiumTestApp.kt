package org.readium.r2.testapp.compose

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import org.readium.r2.testapp.R
import org.readium.r2.testapp.compose.about.aboutScreen
import org.readium.r2.testapp.compose.bookshelf.bookshelfScreen
import org.readium.r2.testapp.compose.catalogs.cataloglist.catalogListScreen
import org.readium.r2.testapp.compose.catalogs.catalogdetail.catalogScreen
import org.readium.r2.testapp.compose.catalogs.catalogdetail.navigateToCatalog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadiumTestApp() {

    TestAppTheme {

        val appState = rememberTestAppState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(id = R.string.app_title))
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
                modifier = Modifier.padding(it).fillMaxSize(),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None },
            ) {
                bookshelfScreen(
                    onOpenBook = { bookId ->

                    }
                )

                catalogListScreen(
                    onCatalogSelected = { href, title, type ->
                        appState.navController.navigateToCatalog(href, title, type)
                    }
                )

                catalogScreen(
                    onPublicationSelected = {

                    },
                    onCatalogSelected = { href, title, type ->
                        appState.navController.navigateToCatalog(href, title, type)
                    }
                )

                aboutScreen()
            }
        }
    }
}
