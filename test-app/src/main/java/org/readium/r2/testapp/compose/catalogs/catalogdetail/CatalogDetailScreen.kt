package org.readium.r2.testapp.compose.catalogs.catalogdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.readium.r2.shared.publication.Publication
import org.readium.r2.testapp.compose.bookshelf.Loading
import timber.log.Timber

@Composable
internal fun CatalogDetailScreen(
    viewModel: CatalogDetailViewModel = viewModel(),
    catalogId: Long?,
    onPublicationSelected: (Publication) -> Unit,
    onCatalogSelected: (Long) -> Unit,
) {
    val uiState by viewModel.catalogUiState.collectAsStateWithLifecycle()

    when (uiState) {
        CatalogUiState.Loading -> Loading()
        is CatalogUiState.Success -> {
            
        }

        is CatalogUiState.Failed -> Unit
    }
}
