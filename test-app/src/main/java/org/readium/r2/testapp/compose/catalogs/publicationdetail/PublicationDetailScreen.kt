package org.readium.r2.testapp.compose.catalogs.publicationdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.readium.r2.testapp.compose.bookshelf.Loading

@Composable
internal fun PublicationDetailScreen(
    modifier: Modifier = Modifier,
    publicationDetailViewModel: PublicationDetailViewModel,
) {
    val uiState by publicationDetailViewModel.publicationDetailUiState.collectAsStateWithLifecycle()

    when (val currentState = uiState) {
        PublicationDetailUiState.Loading -> Loading()
        is PublicationDetailUiState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
            ) {
                currentState.publication.metadata.title?.let { Text(text = it) }
            }
        }

        is PublicationDetailUiState.Failed -> Text(text = "Error: ${currentState.error}")
    }
}
