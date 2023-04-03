package io.github.curo.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class SearchViewModel : FeedViewModel() {
    private var _query: String by mutableStateOf("")
    var query
        get() = _query
        set(value) {
            items.clear()
            if (value == _query) return
            _query = value
            items.addAll(
                super.items.filter { item -> item.name.contains(value, true) }
            )
        }
    override val items: MutableList<Note> = mutableStateListOf()
}