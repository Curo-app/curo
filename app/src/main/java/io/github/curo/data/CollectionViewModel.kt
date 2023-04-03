package io.github.curo.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
open class CollectionViewModel : FeedViewModel() {

    private val _collections = mutableStateListOf<CollectionPreviewModel>()
    val collections: List<CollectionPreviewModel> get() = _collections

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val items = loadItems()
                val data = items
                    .flatMap { it.collections }
                    .distinct()
                    .map { name ->
                        val notes = items.filter { note ->
                            name in note.collections
                        }
                        CollectionPreviewModel(name = name.name, notes = notes)
                    }

                withContext(Dispatchers.Main) {
                    _collections.addAll(data)
                }
            }
        }
    }

    private val _expanded = mutableStateListOf<String>()
    val expanded: List<String> get() = _expanded

    private val _suggestions = mutableStateListOf<String>()
    val suggestions: List<String> get() = _suggestions

    private var _query: String by mutableStateOf("")
    var query
        get() = _query
        set(value) {
            _suggestions.clear()
            if (value == _query) return
            _query = value
            val collectionsNames = items
                .flatMap { it.collections }
                .distinct()
                .map { it.name }
            _suggestions.addAll(collectionsNames)
        }


    fun expand(name: String) {
        val existing = _expanded.indexOfFirst { it == name }
        if (existing == -1) {
            _expanded.add(name)
        } else {
            _expanded.removeAt(existing)
        }
    }

    fun update(collection: CollectionPreviewModel) {
        val index = _collections.indexOfFirst { it.name == collection.name }
        if (index == -1) {
            _collections.add(collection)
        } else {
            _collections[index] = collection
        }
    }

    fun addNote(note: Note) {
        val noteCollectionNames = note.collections.map { it.name }
        // updating existing collections
        _collections.replaceAll { collection ->
            if (collection.name in noteCollectionNames) {
                val notExists = collection.notes.none { it.id == note.id }

                CollectionPreviewModel(
                    name = collection.name,
                    notes = if (notExists) {
                        collection.notes + note
                    } else {
                        collection.notes.map {
                            if (it.id == note.id) {
                                note
                            } else {
                                it
                            }
                        }
                    }
                )
            } else {
                collection
            }
        }
        // creating new collections
        val existingNames = _collections.map { it.name }
        noteCollectionNames.filter {
            it !in existingNames
        }.forEach { name ->
            _collections.add(
                CollectionPreviewModel(
                    name = name,
                    notes = listOf(note)
                )
            )
        }
    }

    fun delete(collection: CollectionName) {
        _collections.removeIf { it.name == collection.name }
    }
}