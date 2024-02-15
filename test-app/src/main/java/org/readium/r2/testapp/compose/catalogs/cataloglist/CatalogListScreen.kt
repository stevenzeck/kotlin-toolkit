package org.readium.r2.testapp.compose.catalogs.cataloglist

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.readium.r2.testapp.compose.bookshelf.Loading

@Composable
internal fun CatalogListScreen(
    viewModel: CatalogListViewModel = viewModel(),
    onCatalogSelected: (Long) -> Unit
) {
    val listState = rememberLazyListState()
    val uiState by viewModel.catalogListUiState.collectAsStateWithLifecycle()

    when (uiState) {
        CatalogListUiState.Loading -> Loading()
        is CatalogListUiState.Success -> if ((uiState as CatalogListUiState.Success).catalogs.isNotEmpty()) {
            LazyColumn(state = listState, contentPadding = PaddingValues(10.dp)) {
                items(
                    items = (uiState as CatalogListUiState.Success).catalogs,
                    key = { catalog ->
                        catalog.id!!
                    }
                ) { catalog ->
                    Button(onClick = { onCatalogSelected(catalog.id!!) }) {
                        Text(catalog.title)
                    }
                }
            }
        }

        is CatalogListUiState.Failed -> Unit
    }
}
