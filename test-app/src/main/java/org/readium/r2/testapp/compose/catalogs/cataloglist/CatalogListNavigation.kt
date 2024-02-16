package org.readium.r2.testapp.compose.catalogs.cataloglist

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.readium.r2.testapp.compose.Screen

fun NavGraphBuilder.catalogListScreen(
    onCatalogSelected: (href: String, title: String, type: Int) -> Unit
) {
    composable(Screen.BottomNav.Catalogs.route) {
        CatalogListScreen(
            onCatalogSelected = onCatalogSelected
        )
    }
}
