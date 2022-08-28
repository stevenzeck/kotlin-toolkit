package org.readium.r2.testapp.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource

@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Composable
fun ReadiumTestApp() {

        MaterialTheme {

            val appState = rememberTestAppState()

            TestAppScaffold(
                topBar = {
                    SmallTopAppBar(
                        title = {
                            Text(text = appState.topBarState.title)
                        },
                        navigationIcon = {
                            if (appState.showBackButton) {
                                IconButton(onClick = appState::upPress) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = stringResource(id = R.string.back),
                                    )
                                }
                            } else appState.topBarState.navigationAction?.invoke()
                        },
                        actions = {
                            appState.topBarState.actions?.invoke(this)
                        }
                    )
                },
                floatingActionButtonPosition = FabPosition.End,
                floatingActionButton = {
                    if (appState.showFab) {
                        FloatingActionButton(
                            onClick = {

                            },
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                //TODO make this dynamic based on screen
                                contentDescription = stringResource(id = R.string.add_book)
                            )
                        }
                    }
                },
                bottomBar = {
                    TestAppBottomBar(
                        appState,
                        remember { BottomNavTabs.values() }
                    )
                }
            ) {
                NavGraph(
                    appState = appState
                )
            }
        }

}