package org.readium.r2.testapp.compose

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.readium.r2.opds.OPDS1Parser
import org.readium.r2.opds.OPDS2Parser
import org.readium.r2.shared.opds.ParseData
import org.readium.r2.shared.util.Try
import org.readium.r2.shared.util.http.HttpRequest
import org.readium.r2.testapp.catalogs.CatalogRepository
import org.readium.r2.testapp.db.BookDatabase
import org.readium.r2.testapp.domain.model.Catalog
import org.readium.r2.testapp.utils.Result
import timber.log.Timber
import java.net.MalformedURLException

class CatalogDetailViewModel(application: Application): AndroidViewModel(application) {

    private val catalogDao = BookDatabase.getDatabase(application).catalogDao()
    private val repository = CatalogRepository(catalogDao)

    private val _uiState = MutableStateFlow(CatalogDetailUiState(loading = true))
    val uiState: StateFlow<CatalogDetailUiState> = _uiState.asStateFlow()

    fun fetchCatalog(catalogId: Long) = viewModelScope.launch {
        val catalog = repository.getCatalog(catalogId)
        val parsedCatalog = catalog?.let { parseCatalog(it) }
        _uiState.update {
            when (parsedCatalog) {
                is Result.Success -> it.copy(catalog = parsedCatalog.data, loading = false)
                else -> it.copy(loading = false)
            }
        }
    }

    private suspend fun parseCatalog(catalog: Catalog): Result<ParseData> {
        var parseRequest: Try<ParseData, Exception>?
        catalog.href.let {
            val request = HttpRequest(it)
            try {
                parseRequest = if (catalog.type == 1) {
                    OPDS1Parser.parseRequest(request)
                } else {
                    OPDS2Parser.parseRequest(request)
                }
            } catch (e: MalformedURLException) {
                return Result.Error(e)
            }
        }
        parseRequest?.onSuccess {
            return Result.Success(it)
        }
        parseRequest?.onFailure {
            Timber.e(it)
            return Result.Error(it)
        }
        return Result.Error(Exception(""))
    }
}

data class CatalogDetailUiState(
    val catalog: ParseData? = null,
    val loading: Boolean = false
) {
    /**
     * True if the post couldn't be found
     */
    val failedLoading: Boolean
        get() = !loading && catalog == null
}