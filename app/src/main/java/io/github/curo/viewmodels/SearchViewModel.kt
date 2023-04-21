package io.github.curo.viewmodels

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.curo.data.NotePreviewModel

@Stable
class SearchViewModel : FeedViewModel() {
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
    override val notes: MutableList<NotePreviewModel> = mutableStateListOf()
}