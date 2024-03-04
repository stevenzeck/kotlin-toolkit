package org.readium.r2.testapp.compose.catalogs.catalogdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.readium.r2.shared.opds.Group
import org.readium.r2.shared.publication.Link
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.opds.images
import org.readium.r2.testapp.compose.BookCover
import org.readium.r2.testapp.compose.bookshelf.Loading
import org.readium.r2.testapp.data.model.Catalog

@Composable
internal fun CatalogDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: CatalogDetailViewModel = viewModel(),
    onPublicationSelected: (Publication) -> Unit,
    onCatalogSelected: (String, String, Int) -> Unit,
) {
    val uiState by viewModel.catalogUiState.collectAsStateWithLifecycle()

    when (uiState) {
        is CatalogUiState.Loading -> Loading()
        is CatalogUiState.Success -> {
            Column(modifier = modifier.fillMaxSize()) {
                NavigationList(
                    type = (uiState as CatalogUiState.Success).parseData.feed?.type,
                    navigationLinks = (uiState as CatalogUiState.Success).parseData.feed?.navigation,
                    onCatalogSelected = onCatalogSelected
                )
                (uiState as CatalogUiState.Success).parseData.feed?.publications?.let {
                    PublicationsList(
                        publications = it,
                        onPublicationSelected = onPublicationSelected
                    )
                }
            }
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
                    Text(text = link.title!!)
                }
            }
        }
    }
}

@Composable
fun PublicationsList(
    publications: List<Publication>,
    onPublicationSelected: (Publication) -> Unit,
) {
    LazyRow {
        items(publications) { publication ->
            val coverImage = publication.linkWithRel("http://opds-spec.org/image/thumbnail")?.href
                ?: publication.images.firstOrNull()?.href
            BookCover(
                title = publication.metadata.title,
                coverImageHref = coverImage.toString(),
                onItemSelected = { onPublicationSelected(publication) }
            )
        }
    }
}

@Composable
fun GroupList(
    groups: List<Group>
) {

}
