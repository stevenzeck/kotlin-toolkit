package org.readium.r2.testapp.compose.catalogs.publicationdetail

import org.readium.r2.shared.publication.Publication

sealed interface PublicationDetailUiState {

    data object Loading : PublicationDetailUiState

    data class Failed(
        val error: Exception
    ) : PublicationDetailUiState

    data class Success(
        val publication: Publication,
    ) : PublicationDetailUiState
}