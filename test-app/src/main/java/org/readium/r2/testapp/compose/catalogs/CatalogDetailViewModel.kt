package org.readium.r2.testapp.compose.catalogs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.readium.r2.opds.OPDS1Parser
import org.readium.r2.opds.OPDS2Parser
import org.readium.r2.shared.opds.ParseData
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.Try
import org.readium.r2.shared.util.http.HttpRequest
import org.readium.r2.testapp.data.model.Catalog
import timber.log.Timber

class CatalogDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CatalogDetailUiState(loading = true))
    val uiState: StateFlow<CatalogDetailUiState> = _uiState.asStateFlow()

    private val app = getApplication<org.readium.r2.testapp.Application>()

    fun parseCatalog(catalog: Catalog) = viewModelScope.launch {
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
        parseRequest?.onSuccess { parseData ->
            _uiState.update { uiState ->
                uiState.copy(catalog = parseData, loading = false)
            }
        }
        parseRequest?.onFailure {
            _uiState.update { uiState ->
                uiState.copy(loading = false)
            }
            Timber.e(it)
        }
    }
}

data class CatalogDetailUiState(
    val catalog: ParseData? = null,
    val loading: Boolean = false
)
