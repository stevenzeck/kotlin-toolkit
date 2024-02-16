package org.readium.r2.testapp.compose.catalogs.catalogdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.readium.r2.shared.opds.Group
import org.readium.r2.shared.publication.Link
import org.readium.r2.shared.publication.Publication
import org.readium.r2.testapp.compose.bookshelf.Loading
import org.readium.r2.testapp.data.model.Catalog

@Composable
internal fun CatalogDetailScreen(
    viewModel: CatalogDetailViewModel = viewModel(),
    onPublicationSelected: (Publication) -> Unit,
    onCatalogSelected: (String, String, Int) -> Unit,
) {
    val uiState by viewModel.catalogUiState.collectAsStateWithLifecycle()

    when (uiState) {
        is CatalogUiState.Loading -> Loading()
        is CatalogUiState.Success -> {

            NavigationList(
                type = (uiState as CatalogUiState.Success).parseData.feed?.type,
                navigationLinks = (uiState as CatalogUiState.Success).parseData.feed?.navigation,
                onCatalogSelected = onCatalogSelected
            )

        }

        is CatalogUiState.Failed -> Unit
    }
}

@Composable
fun NavigationList(
    type: Int?,
    navigationLinks: List<Link>?,
    onCatalogSelected: (String, String, Int) -> Unit,
) {
    Column {
        if (navigationLinks != null) {
            for (link in navigationLinks) {
                val catalog = type?.let {
                    Catalog(
                        href = link.href.toString(),
                        title = link.title!!,
                        type = it
                    )
                }
                Button(onClick = {
                    if (catalog != null) {
                        onCatalogSelected(catalog.href, catalog.title, catalog.type)
                    }
                }) {
                    Text(link.title!!)
                }
            }
        }
    }
}

@Composable
fun PublicationsList(
    publications: List<Publication>
) {

}

@Composable
fun GroupList(
    groups: List<Group>
) {

}
