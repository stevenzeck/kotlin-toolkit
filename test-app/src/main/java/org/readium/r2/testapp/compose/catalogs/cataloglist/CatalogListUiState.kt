package org.readium.r2.testapp.compose.catalogs.cataloglist

import org.readium.r2.testapp.data.model.Catalog

sealed interface CatalogListUiState {

    data object Loading : CatalogListUiState

    data class Failed(
        val errorMessages: List<String>
    ) : CatalogListUiState

    data class Success(
        val catalogs: List<Catalog>,
    ) : CatalogListUiState
}
