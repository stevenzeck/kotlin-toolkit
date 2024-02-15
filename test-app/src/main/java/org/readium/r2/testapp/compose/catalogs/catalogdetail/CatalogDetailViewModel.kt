package org.readium.r2.testapp.compose.catalogs.catalogdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.readium.r2.opds.OPDS1Parser
import org.readium.r2.opds.OPDS2Parser
import org.readium.r2.shared.opds.ParseData
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.Try
import org.readium.r2.shared.util.getOrThrow
import org.readium.r2.shared.util.http.HttpRequest
import org.readium.r2.testapp.data.CatalogRepository
import org.readium.r2.testapp.data.db.AppDatabase
import org.readium.r2.testapp.data.model.Catalog

class CatalogDetailViewModel(application: Application, savedStateHandle: SavedStateHandle) :
    AndroidViewModel(application) {

    private val app = getApplication<org.readium.r2.testapp.Application>()
    private val catalogDao = AppDatabase.getDatabase(application).catalogDao()
    private val repository = CatalogRepository(catalogDao)
    private val catalogId: Long = savedStateHandle.get<Long>("catalogId") ?: 0L

    val catalogUiState: StateFlow<CatalogUiState> =
        repository.getCatalogFromDatabase(catalogId)
            .map { catalog -> parseCatalog(catalog) }
            .map { parseData ->
                if (parseData != null) {
                    CatalogUiState.Success(parseData)
                } else {
                    CatalogUiState.Failed(Exception("Failed to parse catalog"))
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = CatalogUiState.Loading,
            )

    suspend fun parseCatalog(catalog: Catalog): ParseData? {
        var parseRequest: Try<ParseData, Exception>? = null
        catalog.href.let { href ->
            AbsoluteUrl(href)
                ?.let { HttpRequest(it) }
                ?.let { request ->
                    parseRequest = if (catalog.type == 1) {
                        OPDS1Parser.parseRequest(request, app.readium.httpClient)
                    } else {
                        OPDS2Parser.parseRequest(request, app.readium.httpClient)
                    }
                }
        }
        return parseRequest?.getOrThrow()
    }
}
