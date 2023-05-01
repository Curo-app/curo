package io.github.curo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Transaction
import io.github.curo.data.CollectionPreview
import io.github.curo.database.dao.CollectionDao
import io.github.curo.database.dao.NoteCollectionCrossRefDao
import io.github.curo.database.dao.NoteDao
import io.github.curo.database.entities.*
import io.github.curo.database.entities.Collection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RealCollectionViewModel(
    private val noteDao: NoteDao,
    private val collectionDao: CollectionDao,
    private val noteCollectionCrossRefDao: NoteCollectionCrossRefDao
) : ViewModel() {
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

    fun getAll(): Flow<List<CollectionPreview>> =
        collectionDao.getAll()
            .map { l -> l.map { CollectionPreview.of(it) } }

    fun find(collectionName: String): Flow<CollectionPreview?> =
        collectionDao.find(collectionName)
            .map { collection -> collection?.let { CollectionPreview.of(it) } }


    class RealCollectionViewModelFactory(
        private val noteDao: NoteDao,
        private val collectionDao: CollectionDao,
        private val noteCollectionCrossRefDao: NoteCollectionCrossRefDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RealCollectionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RealCollectionViewModel(
                    noteDao,
                    collectionDao,
                    noteCollectionCrossRefDao
                ) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }
}