package io.github.curo.viewmodels

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.github.curo.data.CollectionPreview
import io.github.curo.data.NotePreview.Companion.extractCollections
import io.github.curo.database.dao.CollectionDao
import io.github.curo.database.dao.NoteCollectionCrossRefDao
import io.github.curo.database.dao.NoteDao
import io.github.curo.database.entities.CollectionInfo
import io.github.curo.database.entities.CollectionWithNotes.Companion.toCollectionPreviews
import io.github.curo.utils.NOT_FOUND_INDEX
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class CollectionUiState(
    val collections: List<CollectionPreview> = mutableListOf()
)

@Stable
open class CollectionViewModel(
    noteDao: NoteDao,
    collectionDao: CollectionDao,
    noteCollectionCrossRefDao: NoteCollectionCrossRefDao
) : FeedViewModel(noteDao) {

//    private val _collections = mutableStateListOf<CollectionPreview>()
//    val collections: List<CollectionPreview> get() = _collections

    val collectionUiState: StateFlow<CollectionUiState> =
        collectionDao.getAll()
            .map { collections ->
                collections.toCollectionPreviews()
            }
            .map { notes ->
                CollectionUiState(notes)
            }
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = CollectionUiState()
            )

//    init {
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                val items = loadItems()
//                val data = items
//                    .extractCollections()
//                    .map { name ->
//                        val notes = items.filter { note ->
//                            name in note.collections
//                        }
//                        CollectionPreview(
//                            id = name.collectionId,
//                            name = name.collectionName,
//                            notes = notes
//                        )
//                    }
//
//                withContext(Dispatchers.Main) {
//                    _collections.addAll(data)
//                }
//            }
//        }
//    }

    var expanded = mutableStateListOf<Long>()
        private set
    var suggestions = mutableStateListOf<CollectionInfo>()
        private set

    var query = mutableStateOf("")
        private set(value) {
            suggestions.clear()
            if (value == query) return
            field = value
            val collectionsNames =
                notes.extractCollections()
            suggestions.addAll(collectionsNames)
        }


    fun expand(id: Long) {
        val existing = expanded.indexOfFirst { it == id }
        if (existing == NOT_FOUND_INDEX) {
            expanded.add(id)
        } else {
            expanded.removeAt(existing)
        }
    }

    fun isExpanded(id: Long): Boolean {
        return id in expanded
    }

//    fun update(collection: CollectionPreview) {
//        val index = _collections.indexOfFirst { it.name == collection.name }
//        if (index == NOT_FOUND_INDEX) {
//            _collections.add(collection)
//        } else {
//            _collections[index] = collection
//        }
//    }

//    fun addNote(note: NotePreview) {
//        val noteCollectionNames = note.collections
//        // updating existing collections
//        _collections.replaceAll { collection ->
//            if (collection.id in noteCollectionNames.map { it.collectionId }) {
//                val notExists = collection.notes.none { it.id == note.id }
//
//                CollectionPreview(
//                    name = collection.name,
//                    notes = if (notExists) {
//                        collection.notes + note
//                    } else {
//                        collection.notes.map {
//                            if (it.id == note.id) {
//                                note
//                            } else {
//                                it
//                            }
//                        }
//                    }
//                )
//            } else {
//                collection
//            }
//        }
//        // creating new collections
//        val existingNames = _collections.map { CollectionInfo(it.id, it.name) }
//        noteCollectionNames.filter {
//            it !in existingNames
//        }.forEach { name ->
//            _collections.add(
//                CollectionPreview(
//                    id = name.collectionId,
//                    name = name.collectionName,
//                    notes = listOf(note)
//                )
//            )
//        }
//    }

//    fun delete(name: String) {
//        _collections.removeIf { it.name == name }
//    }

    class CollectionViewModelFactory(
        private val noteDao: NoteDao,
        private val collectionDao: CollectionDao,
        private val noteCollectionCrossRefDao: NoteCollectionCrossRefDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CollectionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CollectionViewModel(noteDao, collectionDao, noteCollectionCrossRefDao) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }
}