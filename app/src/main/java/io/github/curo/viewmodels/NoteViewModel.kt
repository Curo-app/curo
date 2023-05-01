package io.github.curo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Transaction
import io.github.curo.data.NotePreview
import io.github.curo.database.dao.NoteCollectionCrossRefDao
import io.github.curo.database.entities.Note
import io.github.curo.database.dao.NoteDao
import io.github.curo.database.entities.NoteCollectionCrossRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteViewModel(
    private val noteDao: NoteDao,
    private val noteCollectionCrossRefDao: NoteCollectionCrossRefDao
) : ViewModel() {
    @Transaction
    suspend fun insert(notePreview: NotePreview) {
        val noteId = noteDao.insert(Note.of(notePreview))
        val crossRefs = notePreview.collections
            .map { NoteCollectionCrossRef(noteId, it) }
        noteCollectionCrossRefDao.insertAll(crossRefs)
    }

    suspend fun delete(noteId: Long) = noteDao.delete(noteId)

    @Transaction
    suspend fun update(notePreview: NotePreview) {
        noteDao.update(Note.of(notePreview))
        noteCollectionCrossRefDao.deleteAllByNoteId(notePreview.id)
        val crossRefs = notePreview.collections
            .map { NoteCollectionCrossRef(notePreview.id, it) }
        noteCollectionCrossRefDao.insertAll(crossRefs)
    }


    fun getAll(): Flow<List<NotePreview>> =
        noteDao.getAll()
            .map { l -> l.map { NotePreview.of(it) } }

    fun find(noteId: Long): Flow<NotePreview?> =
        noteDao.find(noteId)
            .map { note -> note?.let { NotePreview.of(it) } }

    suspend fun markCompleted(noteId: Long) =
        noteDao.markCompleted(noteId)

    class NoteViewModelFactory(
        private val noteDao: NoteDao,
        private val noteCollectionCrossRefDao: NoteCollectionCrossRefDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NoteViewModel(noteDao, noteCollectionCrossRefDao) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }

    }
}