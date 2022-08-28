package org.readium.r2.testapp.catalogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import org.readium.r2.testapp.compose.CatalogDetailViewModel

@Composable
fun CatalogDetailScreen(viewModel: CatalogDetailViewModel = viewModel(), catalogId: Long?) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(catalogId) {
        catalogId?.let { viewModel.fetchCatalog(catalogId) }
    }

    if (uiState.catalog != null) {
        Column {
            uiState.catalog!!.feed?.navigation?.forEach {
                Button(onClick = {  }) {
                    it.title?.let { it1 -> Text(it1) }
                }
            }
        }
    }
}