package io.github.curo.viewmodels

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Transaction
import io.github.curo.data.CollectionPreview
import io.github.curo.data.NotePreview
import io.github.curo.database.dao.CollectionDao
import io.github.curo.database.dao.NoteCollectionCrossRefDao
import io.github.curo.database.dao.NoteDao
import io.github.curo.database.entities.Collection
import io.github.curo.database.entities.Note
import io.github.curo.database.entities.NoteCollectionCrossRef
import io.github.curo.utils.setAll

@Stable
class CollectionPatchViewModel(
    private val noteDao: NoteDao,
    private val collectionDao: CollectionDao,
    private val noteCollectionCrossRefDao: NoteCollectionCrossRefDao
) : FeedViewModel() {
    var name: String by mutableStateOf("New collection")
    override val notes: MutableList<NotePreview> = mutableStateListOf()
    // TODO: make id as a primary key instead of saving previous name
    var oldName: String by mutableStateOf("")
    var createdNoteIds: MutableList<Long> = mutableStateListOf()

    @Transaction
    suspend fun insert(collectionPreview: CollectionPreview) {
        collectionDao.insert(Collection.of(collectionPreview))
        val notes = collectionPreview.notes.map { Note.of(it) }
        val noteIds = noteDao.insertAll(notes)
        val crossRefs = noteIds.map { NoteCollectionCrossRef(it, collectionPreview.name) }
        noteCollectionCrossRefDao.insertAll(crossRefs)
    }

    // deletes collection and relationship
    suspend fun delete(collectionName: String) = collectionDao.delete(collectionName)

    // deletes collection with all notes
    @Transaction
    suspend fun deleteCascade(collectionName: String) =
        noteCollectionCrossRefDao.listByCollectionName(collectionName)
            .collect { crossRefs ->
                collectionDao.delete(collectionName)
                noteDao.deleteAll(crossRefs.map { it.noteId })
            }

    fun clear() {
        name = ""
        notes.clear()
        createdNoteIds.clear()
    }

    fun set(name: String) {
        if (name == this.name) return
        this.name = name
        this.notes.setAll(
            super.notes.filter { item -> name in item.collections }
        )
    }

    fun setCollection(collection: CollectionPreview) {
        if (name == this.name) return
        name = collection.name
        notes.setAll(collection.notes)
    }

    fun toCollection() = CollectionPreview(
        name = name,
        notes = notes,
    )

    suspend fun save() {
        val collection = toCollection()
        delete(oldName) // TODO: remove this workaround
        insert(collection)
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
