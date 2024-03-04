package org.readium.r2.testapp.compose

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import org.readium.r2.testapp.compose.catalogs.catalogdetail.catalogScreen
import org.readium.r2.testapp.compose.catalogs.catalogdetail.navigateToCatalog
import org.readium.r2.testapp.compose.catalogs.cataloglist.catalogListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadiumTestApp() {

    TestAppTheme {

        val appState = rememberTestAppState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.app_title))
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
                        imageVector = Icons.Filled.Add,
                        contentDescription = "TODO"
                    )
                }

            },
            bottomBar = {
                TestAppBottomBar(
                    navController = appState.navController
                )
            }
        ) {
            NavHost(
                navController = appState.navController,
                startDestination = Screen.BottomNav.Bookshelf.route,
                modifier = Modifier
                    .padding(it),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
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
