package io.github.curo.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
class CalendarViewModel : FeedViewModel() {
    private val _collectionsNames = mutableStateMapOf<CollectionName, CollectionFilter>()
    val collectionsNames: List<CollectionFilter> get() = _collectionsNames.values.toList()

    private val _items = mutableStateListOf<Note>()
    override val items: List<Note>
        get() = _items

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val items = loadItems()
                val data = items
                    .flatMap { it.collections }
                    .distinct()
                    .associateWith { CollectionFilter(it) }

                withContext(Dispatchers.Main) {
                    _items.addAll(items)
                    _collectionsNames.putAll(data)
                }
            }
        }
    }

    private fun resetFilters() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val items = loadItems()
                withContext(Dispatchers.Main) {
                    _items.clear()
                    _items.addAll(items)
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

        val filteredNotes = super.items.filter { note ->
            note.collections.any { it in showedCollections }
        }
        _items.clear()
        _items.addAll(filteredNotes)
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