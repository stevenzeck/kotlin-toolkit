package org.readium.r2.testapp.compose.catalogs

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.readium.r2.testapp.data.CatalogRepository
import org.readium.r2.testapp.data.db.AppDatabase
import org.readium.r2.testapp.data.model.Catalog

class CatalogListViewModel(application: Application) : AndroidViewModel(application) {

    private val catalogDao = AppDatabase.getDatabase(application).catalogDao()
    private val repository = CatalogRepository(catalogDao)
    val version = 2
    val VERSION_KEY = "OPDS_CATALOG_VERSION"

    private val viewModelState = MutableStateFlow(CatalogListViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    // FIXME ugly
    init {
        runBlocking {
            val preferences =
                application.getSharedPreferences("org.readium.r2.testapp", Context.MODE_PRIVATE)
            if (preferences.getInt(VERSION_KEY, 0) < version) {
                preferences.edit().putInt(VERSION_KEY, version).apply()
                val oPDS2Catalog = Catalog(
                    title = "OPDS 2.0 Test Catalog",
                    href = "https://test.opds.io/2.0/home.json",
                    type = 2
                )
                val oTBCatalog = Catalog(
                    title = "Open Textbooks Catalog",
                    href = "http://open.minitex.org/textbooks/",
                    type = 1
                )

                insertCatalog(oPDS2Catalog)
                insertCatalog(oTBCatalog)
            }
        }
        fetchCatalogs()
    }

    private fun fetchCatalogs() {
        viewModelState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val catalogs = repository.getCatalogsFromDatabase()
//            viewModelState.update {
//                when (catalogs) {
//                    is Try.Success -> it.copy(catalogs = catalogs.value!!, isLoading = false)
//                    else -> {
//                        val errorMessages = it.errorMessages
//                        it.copy(errorMessages = errorMessages, isLoading = false)
//                    }
//                }
//            }
        }
    }

    fun insertCatalog(catalog: Catalog) = viewModelScope.launch {
        repository.insertCatalog(catalog)
    }

    fun deleteCatalog(id: Long) = viewModelScope.launch {
        repository.deleteCatalog(id)
    }
}

sealed interface CatalogListUiState {

    val isLoading: Boolean
    val errorMessages: List<String>

    data class NoCatalogs(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : CatalogListUiState

    data class HasCatalogs(
        val catalogs: List<Catalog>,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : CatalogListUiState
}

private data class CatalogListViewModelState(
    val catalogs: List<Catalog> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
) {

    fun toUiState(): CatalogListUiState =
        if (catalogs.isEmpty()) {
            CatalogListUiState.NoCatalogs(
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        } else {
            CatalogListUiState.HasCatalogs(
                catalogs = catalogs,
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        }
}