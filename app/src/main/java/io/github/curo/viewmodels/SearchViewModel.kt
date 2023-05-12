package io.github.curo.viewmodels

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.github.curo.data.NotePreview
import io.github.curo.database.dao.NoteDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Stable
class SearchViewModel(
    noteDao: NoteDao
) : FeedViewModel(noteDao) {
    private var _query = MutableStateFlow("")
    var query
        get() = _query.value
        set(value) {
            _query.value = value
        }

    override val feedUiState: StateFlow<FeedUiState> =
        _query
            .map { noteDao.searchNotes(it).first() }
            .map { notes -> notes.map { NotePreview.of(it) } }
            .map { notes -> FeedUiState(notes) }
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = FeedUiState(emptyList())
            )

    class SearchViewModelFactory(
        private val noteDao: NoteDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SearchViewModel(noteDao) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }
}