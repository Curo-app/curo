package io.github.curo.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.github.curo.data.CollectionPreview
import io.github.curo.data.NotePreview
import io.github.curo.data.ShareScreenData
import io.github.curo.http.ApiService
import io.github.curo.http.RetrofitBuilder
import io.github.curo.http.toDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class ShareScreenViewModel(
    private val api: ApiService,
) : ViewModel() {
    var link: ShareScreenData by mutableStateOf(ShareScreenData.Hidden)

    fun share(note: NotePreview) {
        link = ShareScreenData.Loading
        viewModelScope.launch {
            flow { emit(api.share(note.toDto())) }
                .flowOn(Dispatchers.IO)
                .catch { link = ShareScreenData.Error }
                .collect { link = ShareScreenData.Url(it.url) }
        }
    }

    fun share(note: CollectionPreview) {
        link = ShareScreenData.Loading
        viewModelScope.launch {
            flow { emit(api.share(note.toDto())) }
                .flowOn(Dispatchers.IO)
                .catch { link = ShareScreenData.Error }
                .collect { link = ShareScreenData.Url(it.url) }
        }
    }

    class ShareScreenViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ShareScreenViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ShareScreenViewModel(RetrofitBuilder.apiService) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }
}