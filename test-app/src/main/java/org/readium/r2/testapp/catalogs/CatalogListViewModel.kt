package org.readium.r2.testapp.compose

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.readium.r2.testapp.db.BookDatabase
import org.readium.r2.testapp.db.DataStorePrefs
import org.readium.r2.testapp.domain.model.Catalog
import org.readium.r2.testapp.utils.Result

class CatalogListViewModel(application: Application): AndroidViewModel(application) {

    private val catalogDao = BookDatabase.getDatabase(application).catalogDao()
    private val repository = CatalogRepository(catalogDao)
    val version = 2
    val VERSION_KEY = intPreferencesKey("OPDS_CATALOG_VERSION")

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
            val datastore = DataStorePrefs.getDataStorePrefs(application)
            val storedVersion = datastore?.data?.first()?.get(VERSION_KEY) ?: 0
            if (storedVersion < version) {
                datastore?.edit {
                    it[VERSION_KEY] = version
                }
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
                val sEBCatalog = Catalog(
                    title = "Standard eBooks Catalog",
                    href = "https://standardebooks.org/opds/all",
                    type = 1
                )

                insertCatalog(oPDS2Catalog)
                insertCatalog(oTBCatalog)
                insertCatalog(sEBCatalog)
            }
        }
        fetchCatalogs()
    }

    private fun fetchCatalogs() {
        viewModelState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val catalogs = repository.catalogs()
            viewModelState.update {
                when (catalogs) {
                    is Result.Success -> it.copy(catalogs = catalogs.data, isLoading = false)
                    else -> {
                        val errorMessages = it.errorMessages
                        it.copy(errorMessages = errorMessages, isLoading = false)
                    }
                }
            }
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