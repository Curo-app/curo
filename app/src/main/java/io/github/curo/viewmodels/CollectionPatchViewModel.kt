package io.github.curo.viewmodels

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import io.github.curo.data.CollectionPreview
import io.github.curo.data.NotePreview
import io.github.curo.data.NotePreview.Companion.toNotePreviews
import io.github.curo.database.dao.CollectionDao
import io.github.curo.database.dao.NoteCollectionCrossRefDao
import io.github.curo.database.dao.NoteDao
import io.github.curo.database.entities.Collection
import io.github.curo.utils.setAll
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class CollectionPatchUiState(
    val notes: List<NotePreview> = mutableListOf()
)

@Stable
class CollectionPatchViewModel(
    private val noteDao: NoteDao,
    private val collectionDao: CollectionDao,
    private val noteCollectionCrossRefDao: NoteCollectionCrossRefDao
) : FeedViewModel(noteDao) {
    var id: Long by mutableStateOf(0L)
    var name: String by mutableStateOf("")
    override val notes: MutableList<NotePreview> = mutableStateListOf()

    val collectionPatchUiState: StateFlow<CollectionPatchUiState> =
        // TODO: replace with findById
        collectionDao.getAll()
            .map { collections -> collections.find { it.collection.collectionId == id } }
            .filterNotNull()
            .map { collection -> collection.notes.toNotePreviews() }
            .map { notes -> CollectionPatchUiState(notes) }
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = CollectionPatchUiState()
            )

//    @Transaction
//    suspend fun insert(collectionPreview: CollectionPreview) {
//        collectionDao.insert(Collection.of(collectionPreview))
//        val notes = collectionPreview.notes.map { Note.of(it) }
//        TODO: insert to crossref on note insert
//        val noteIds = noteDao.insertAll(notes)
//        val crossRefs = noteIds.map { NoteCollectionCrossRef(it, collectionPreview.id) }
//        noteCollectionCrossRefDao.insertAll(crossRefs)
//    }

    // deletes collection and relationship
    suspend fun delete(collectionId: Long) = collectionDao.delete(collectionId)

    // deletes collection with all notes
    @Transaction
    suspend fun deleteCascade(collectionId: Long) =
        noteCollectionCrossRefDao.listByCollectionId(collectionId)
            .collect { crossRefs ->
                collectionDao.delete(collectionId)
                noteDao.deleteAll(crossRefs.map { it.noteId })
            }

    fun clear() {
        id = 0L
        name = ""
        notes.clear()
    }

//    fun set(id: Long) {
//        if (this.id == id) return
//        this.id = id
//        this.name = name
//        this.notes.setAll(
//            super.notes.filter { item -> id in item.collections.map { it.collectionId } }
//        )
//    }

    fun setCollection(collection: CollectionPreview) {
        if (this.id == collection.id) return
        id = collection.id
        name = collection.name
        notes.setAll(collection.notes)
    }

    fun toCollectionPreview() = CollectionPreview(
        id = id,
        name = name,
        notes = notes,
    )

    suspend fun updateCollection() {
        val collectionPreview = toCollectionPreview()
        collectionDao.update(Collection.of(collectionPreview))
    }

    suspend fun insertEmpty(): Long {
        val collection = toCollectionPreview()
        id = collectionDao.insert(Collection.of(collection))
        return id
    }

    class CollectionPatchViewModelFactory(
        private val noteDao: NoteDao,
        private val collectionDao: CollectionDao,
        private val noteCollectionCrossRefDao: NoteCollectionCrossRefDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CollectionPatchViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CollectionPatchViewModel(
                    noteDao,
                    collectionDao,
                    noteCollectionCrossRefDao
                ) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }
}
