package org.readium.r2.testapp.compose.catalogs.catalogdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import java.net.URLDecoder
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import org.readium.r2.opds.OPDS1Parser
import org.readium.r2.opds.OPDS2Parser
import org.readium.r2.shared.opds.ParseData
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.Try
import org.readium.r2.shared.util.http.HttpRequest

class CatalogDetailViewModel(application: Application, savedStateHandle: SavedStateHandle) :
    AndroidViewModel(application) {

    private val app = getApplication<org.readium.r2.testapp.Application>()
    private val href: String = savedStateHandle["href"] ?: ""
    private val title: String = savedStateHandle["title"] ?: ""
    private val type: Int = savedStateHandle["type"] ?: 2

    val catalogUiState: StateFlow<CatalogUiState> = flow {
        val result = viewModelScope.async { parseCatalog(href, type) }
        emit(result.await().fold(onSuccess = { parsedData -> CatalogUiState.Success(parsedData) },
            onFailure = { exception -> CatalogUiState.Failed(exception) }))
    }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CatalogUiState.Loading,
        )

    suspend fun parseCatalog(href: String, type: Int): Try<ParseData, Exception> {
        val url = URLDecoder.decode(href, "UTF-8")
        return url.let { url ->
            AbsoluteUrl(url) ?: return@parseCatalog Try.failure(Exception("Invalid href"))
        }.let { HttpRequest(it) }.let { request ->
                when (type) {
                    1 -> OPDS1Parser.parseRequest(request, app.readium.httpClient)
                    2 -> OPDS2Parser.parseRequest(request, app.readium.httpClient)
                    else -> return@parseCatalog Try.failure(Exception("Unsupported catalog type"))
                }
            }
    }
}
