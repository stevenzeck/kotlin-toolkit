package org.readium.r2.testapp.compose.catalogs.cataloglist

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.readium.r2.testapp.compose.catalogs.cataloglist.CatalogListUiState
import org.readium.r2.testapp.data.CatalogRepository
import org.readium.r2.testapp.data.db.AppDatabase
import org.readium.r2.testapp.data.model.Catalog

class CatalogListViewModel(application: Application) : AndroidViewModel(application) {

    private val catalogDao = AppDatabase.getDatabase(application).catalogDao()
    private val repository = CatalogRepository(catalogDao)
    val version = 2
    val VERSION_KEY = "OPDS_CATALOG_VERSION"

    val catalogListUiState: StateFlow<CatalogListUiState> =
        repository.getCatalogsFromDatabase()
            .map(CatalogListUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = CatalogListUiState.Loading,
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
    }

    fun insertCatalog(catalog: Catalog) = viewModelScope.launch {
        repository.insertCatalog(catalog)
    }

    fun deleteCatalog(id: Long) = viewModelScope.launch {
        repository.deleteCatalog(id)
    }
}
