package org.readium.r2.testapp.compose.catalogs.catalogdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.readium.r2.shared.opds.Group
import org.readium.r2.shared.publication.Link
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.opds.images
import org.readium.r2.testapp.R
import org.readium.r2.testapp.compose.BookCover
import org.readium.r2.testapp.compose.bookshelf.Loading
import org.readium.r2.testapp.compose.catalogs.publicationdetail.PublicationDetailViewModel

@Composable
internal fun CatalogDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: CatalogDetailViewModel = viewModel(),
    publicationDetailViewModel: PublicationDetailViewModel,
    onPublicationSelected: () -> Unit,
    onCatalogSelected: (String, String, Int) -> Unit,
) {
    val uiState by viewModel.catalogUiState.collectAsStateWithLifecycle()

    when (val currentState = uiState) {
        is CatalogUiState.Loading -> Loading()
        is CatalogUiState.Success -> {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 120.dp),
                modifier = modifier
                    .fillMaxSize()
            ) {
                item(
                    span = {
                        GridItemSpan(maxLineSpan)
                    }
                ) {
                    NavigationList(
                        type = currentState.parseData.feed?.type,
                        navigationLinks = currentState.parseData.feed?.navigation,
                        onCatalogSelected = onCatalogSelected
                    )
                }
                currentState.parseData.feed?.publications?.let { publications ->
                    items(publications.size) { index ->
                        val publication = publications[index]
                        val coverImage =
                            publication.linkWithRel("http://opds-spec.org/image/thumbnail")?.href
                                ?: publication.images.firstOrNull()?.href
                        BookCover(
                            title = publication.metadata.title,
                            coverImageHref = coverImage.toString(),
                            onItemSelected = {
                                publicationDetailViewModel.updatePublicationSelection(publication)
                                onPublicationSelected()
                            }
                        )
                    }
                }
                currentState.parseData.feed?.groups?.let {
                    item(
                        span = {
                            GridItemSpan(maxLineSpan)
                        }
                    ) {
                        GroupList(
                            type = currentState.parseData.feed?.type,
                            groups = it,
                            onCatalogSelected = onCatalogSelected,
                            onPublicationSelected = { publication ->
                                publicationDetailViewModel.updatePublicationSelection(publication)
                                onPublicationSelected()
                            }
                        )
                    }
                }
            }
        }

        is CatalogUiState.Failed -> Text(text = "Error: ${currentState.error}")
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
                Button(
                    onClick = {
                        if (type != null) {
                            onCatalogSelected(link.href.toString(), link.title!!, type)
                        }
                    }
                ) {
                    Text(text = link.title!!)
                }
            }
        }
    }
}

//TODO make this reusable
@Composable
fun PublicationsHorizontalScrollList(
    publications: List<Publication>,
    onPublicationSelected: (Publication) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
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
    type: Int?,
    groups: List<Group>,
    onCatalogSelected: (String, String, Int) -> Unit,
    onPublicationSelected: (Publication) -> Unit,
) {
    Column {
        groups.forEach { group ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = group.title,
                    style = MaterialTheme.typography.titleLarge
                )
                if (group.links.isNotEmpty() && type != null) {
                    IconButton(
                        onClick = {
                            onCatalogSelected(
                                group.links.first().href.toString(), group.title, type
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowForward,
                            contentDescription = stringResource(id = R.string.catalog_list_more)
                        )
                    }
                }
            }

            PublicationsHorizontalScrollList(
                publications = group.publications,
                onPublicationSelected = onPublicationSelected
            )

            NavigationList(
                type = type,
                navigationLinks = group.navigation,
                onCatalogSelected = onCatalogSelected
            )
        }
    }
}
