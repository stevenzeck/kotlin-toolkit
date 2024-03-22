package org.readium.r2.testapp.compose.catalogs.publicationdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.readium.r2.shared.publication.opds.images
import org.readium.r2.testapp.R
import org.readium.r2.testapp.compose.bookshelf.Loading

@Composable
internal fun PublicationDetailScreen(
    modifier: Modifier = Modifier,
    publicationDetailViewModel: PublicationDetailViewModel,
) {
    val uiState by publicationDetailViewModel.publicationDetailUiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    when (val currentState = uiState) {
        PublicationDetailUiState.Loading -> Loading()
        is PublicationDetailUiState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(currentState.publication.images.firstOrNull()?.href)
                        .build(),
                    placeholder = painterResource(R.drawable.cover),
                    contentDescription = stringResource(R.string.cover_image),
                    contentScale = ContentScale.Crop,
                    modifier = modifier.fillMaxSize()
                )
                currentState.publication.metadata.title?.let { Text(text = it, fontSize = 24.sp) }
                currentState.publication.metadata.description?.let { Text(text = it) }
            }
        }

        is PublicationDetailUiState.Failed -> Text(text = "Error: ${currentState.error}")
    }
}
