package org.readium.r2.testapp.compose.catalogs.catalogdetail

import org.readium.r2.shared.opds.ParseData

sealed interface CatalogUiState {

    data object Loading : CatalogUiState

    data class Failed(
        val error: Exception
    ) : CatalogUiState

    data class Success(
        val parseData: ParseData,
    ) : CatalogUiState
}
