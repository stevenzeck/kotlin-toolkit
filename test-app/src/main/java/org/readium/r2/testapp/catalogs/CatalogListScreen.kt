package org.readium.r2.testapp.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.readium.r2.testapp.bookshelf.Loading

@Composable
fun CatalogListScreen(updateTopBarState: (TopBarState) -> Unit, viewModel: CatalogListViewModel = viewModel(), onCatalogSelected: (Long) -> Unit) {
    val listState = rememberLazyListState()
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is CatalogListUiState.HasCatalogs -> {
            LazyColumn(state = listState, contentPadding = PaddingValues(10.dp)) {
                items(
                    items = (uiState as CatalogListUiState.HasCatalogs).catalogs,
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
        else -> Loading()
    }
}