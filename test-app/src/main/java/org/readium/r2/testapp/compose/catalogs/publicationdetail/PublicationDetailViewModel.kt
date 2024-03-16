package org.readium.r2.testapp.compose.catalogs.publicationdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.readium.r2.shared.publication.Publication

class PublicationDetailViewModel(application: Application) : AndroidViewModel(application) {

//    lateinit var publication: Publication

    private val _publication = MutableStateFlow<Publication?>(null)
    val publication: StateFlow<Publication?> = _publication

    fun updatePublicationSelection(newThing: Publication) {
        _publication.value = newThing
    }

    val publicationDetailUiState: StateFlow<PublicationDetailUiState> =
        publication.map { publication ->
            if (publication != null) {
                PublicationDetailUiState.Success(publication = publication)
            } else {
                PublicationDetailUiState.Failed(Exception("Failed"))
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PublicationDetailUiState.Loading,
            )
}
