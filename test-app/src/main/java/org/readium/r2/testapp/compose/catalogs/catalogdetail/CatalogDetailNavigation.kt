package org.readium.r2.testapp.compose.catalogs.catalogdetail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import java.net.URLEncoder
import org.readium.r2.testapp.compose.Screen

fun NavController.navigateToCatalog(href: String, title: String, type: Int) =
    this.navigate(
        "${Screen.CatalogDetail.route}/${
            URLEncoder.encode(
                href,
                "UTF-8"
            )
        }/${title}/${type}"
    )

fun NavGraphBuilder.catalogScreen(
    onPublicationSelected: () -> Unit,
    onCatalogSelected: (href: String, title: String, type: Int) -> Unit,
) {
    composable(
        route = "${Screen.CatalogDetail.route}/{href}/{title}/{type}",
        arguments = listOf(navArgument("href") { type = NavType.StringType },
            navArgument("title") { type = NavType.StringType },
            navArgument("type") { type = NavType.IntType })
    ) {
        CatalogDetailScreen(
            onPublicationSelected = onPublicationSelected,
            onCatalogSelected = onCatalogSelected
        )
    }
}
