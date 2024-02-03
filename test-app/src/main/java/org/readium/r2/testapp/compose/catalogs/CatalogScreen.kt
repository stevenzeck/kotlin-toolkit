package org.readium.r2.testapp.compose.catalogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.readium.r2.shared.publication.Publication
import org.readium.r2.testapp.compose.Screen
import org.readium.r2.testapp.data.model.Catalog

@Composable
internal fun CatalogScreen(
    viewModel: CatalogDetailViewModel = viewModel(),
    onPublicationSelected: (Publication) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.catalog != null) {
        val parseData = uiState.catalog!!
        Column {
            parseData.feed?.navigation?.forEach {
                Button(onClick = { }) {
                    it.title?.let { it1 -> Text(it1) }
                }
            }
        }
    }
}

fun NavController.navigateToCatalog(catalogId: Long) =
    this.navigate("${Screen.Feed.route}/${catalogId}")

fun NavGraphBuilder.catalogScreen(
    onPublicationSelected: (publication: Publication) -> Unit
) {
    composable("${Screen.Feed.route}/catalogId") {
        CatalogScreen(
            onPublicationSelected = onPublicationSelected
        )
    }
}
