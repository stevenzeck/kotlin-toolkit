/*
 * Copyright 2021 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.r2.testapp.catalogs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.readium.r2.opds.OPDS1Parser
import org.readium.r2.opds.OPDS2Parser
import org.readium.r2.shared.opds.ParseData
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.Try
import org.readium.r2.shared.util.http.HttpRequest
import org.readium.r2.testapp.data.model.Catalog
import timber.log.Timber

class CatalogViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<CatalogUiState>(CatalogUiState.Loading)
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    private val _publication = MutableStateFlow<Publication?>(null)
    val publication: StateFlow<Publication?> = _publication.asStateFlow()

    private val app = getApplication<org.readium.r2.testapp.Application>()

    fun parseCatalog(catalog: Catalog) = viewModelScope.launch {
        _uiState.value = CatalogUiState.Loading
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
            _uiState.value = CatalogUiState.Success(parseData)
        }
        parseRequest?.onFailure {
            Timber.e(it)
            _uiState.value = CatalogUiState.Error("Failed to parse catalog")
        }
    }

    fun setPublication(publication: Publication) {
        _publication.value = publication
    }

    fun downloadPublication(publication: Publication) = viewModelScope.launch {
        app.bookshelf.importPublicationFromOpds(publication)
    }
}

sealed interface CatalogUiState {

    data object Loading : CatalogUiState

    data class Success(
        val parseData: ParseData,
    ) : CatalogUiState

    data class Error(
        val error: String,
    ) : CatalogUiState
}
