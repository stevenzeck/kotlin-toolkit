package org.readium.r2.testapp.catalogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import org.readium.r2.shared.publication.Publication
import org.readium.r2.testapp.MainViewModel
import org.readium.r2.testapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationScreen(
    publication: Publication,
    mainViewModel: MainViewModel,
    viewModel: CatalogViewModel = viewModel()
) {

    LaunchedEffect(Unit) {
        mainViewModel.updateTopBar(title = "Publication")
    }


    PublicationDetailContent(
        publication = publication,
        onDownloadClick = { viewModel.downloadPublication(publication) }
    )

}

@Composable
private fun PublicationDetailContent(
    publication: Publication,
    onDownloadClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(publication.links.firstOrNull { it.rels.contains("cover") }?.href)
                .crossfade(true)
                .build(),
            contentDescription = "Publication cover",
            contentScale = ContentScale.Fit,
            modifier = Modifier.height(240.dp)
        )

        Text(
            text = publication.metadata.title ?: "",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Text(
            text = publication.metadata.authors.joinToString { it.name },
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        publication.metadata.description?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.weight(1.0f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {

            Button(onClick = onDownloadClick) {
                Text(stringResource(id = R.string.catalog_detail_download_button))
            }
        }
    }
}
