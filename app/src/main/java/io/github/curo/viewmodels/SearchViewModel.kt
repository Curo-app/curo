package io.github.curo.viewmodels

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.curo.data.NotePreview
import io.github.curo.database.dao.NoteDao

@Stable
class SearchViewModel(
    noteDao: NoteDao
) : FeedViewModel(noteDao) {
    private var _query: String by mutableStateOf("")
    var query
        get() = _query
        set(value) {
            notes.clear()
            if (value == _query) return
            _query = value
            notes.addAll(
                super.notes.filter { item -> item.name.contains(value, ignoreCase = true) }
            )
        }
    override val notes: MutableList<NotePreview> = mutableStateListOf()

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