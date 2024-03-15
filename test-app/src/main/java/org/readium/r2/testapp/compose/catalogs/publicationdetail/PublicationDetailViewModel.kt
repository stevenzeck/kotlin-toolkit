package org.readium.r2.testapp.compose.catalogs.publicationdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import org.readium.r2.shared.publication.Publication

class PublicationDetailViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var publication: Publication

    val publicationDetailUiState: StateFlow<PublicationDetailUiState> =
        flow {
            if (::publication.isInitialized) {
                emit(PublicationDetailUiState.Success(publication))
            } else {
                emit(PublicationDetailUiState.Failed(Exception("Failed")))
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PublicationDetailUiState.Loading,
            )
}