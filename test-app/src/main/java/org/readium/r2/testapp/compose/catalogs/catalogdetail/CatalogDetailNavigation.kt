package org.readium.r2.testapp.compose.catalogs.catalogdetail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.readium.r2.shared.publication.Publication
import org.readium.r2.testapp.compose.Screen

fun NavController.navigateToCatalog(catalogId: Long) =
    this.navigate("${Screen.CatalogDetail.route}/${catalogId}")

fun NavGraphBuilder.catalogScreen(
    onPublicationSelected: (publication: Publication) -> Unit,
    onCatalogSelected: (Long) -> Unit,
) {
    composable(
        "${Screen.CatalogDetail.route}/{catalogId}",
        arguments = listOf(navArgument("catalogId") { type = NavType.LongType })
    ) {
        CatalogDetailScreen(
            catalogId = it.arguments?.getLong("catalogId"),
            onPublicationSelected = onPublicationSelected,
            onCatalogSelected = onCatalogSelected
        )
    }
}
