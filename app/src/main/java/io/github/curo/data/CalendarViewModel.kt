package io.github.curo.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import io.github.curo.data.Note.Companion.extractCollections
import io.github.curo.utils.setAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
class CalendarViewModel : FeedViewModel() {
    private val _collectionsNames = mutableStateMapOf<CollectionName, CollectionFilter>()
    val collectionsNames: List<CollectionFilter> get() = _collectionsNames.values.toList()

    private val _notes = mutableStateListOf<Note>()
    override val notes: List<Note>
        get() = _notes

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val items = loadItems()
                val collections = items
                    .extractCollections()
                    .associateWith { CollectionFilter(it) }

                withContext(Dispatchers.Main) {
                    _notes.addAll(items)
                    _collectionsNames.putAll(collections)
                }
            }
        }
    }

    private fun resetFilters() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val items = loadItems()
                withContext(Dispatchers.Main) {
                    _notes.setAll(items)
                }
            }
        }
    }

    fun updateOnFilters() {
        val showedCollections = buildSet {
            _collectionsNames.forEach { (name, filter) ->
                if (filter.enabled) {
                    add(name)
                }
            }
        }
        if (showedCollections.isEmpty()) {
            resetFilters()
            return
        }

        val filteredNotes = super.notes.filter { note ->
            note.collections.any { it in showedCollections }
        }
        _notes.setAll(filteredNotes)
    }

    // TODO: change colors
    private val colors = listOf(
        Color(190, 218, 227),
        Color(196, 233, 218),
        Color(254, 213, 207),
        Color(241, 181, 152),
        Color(211, 199, 230)
    )

    @Stable
    data class CollectionFilter(
        val name: CollectionName,
    ) {
        var enabled: Boolean by mutableStateOf(false)
    }
}