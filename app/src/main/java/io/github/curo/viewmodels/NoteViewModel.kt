package io.github.curo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.curo.data.NotePreview
import io.github.curo.database.dao.NoteDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

open class NoteViewModel(
    private val noteDao: NoteDao
) : ViewModel() {
    fun getAll(): Flow<List<NotePreview>> =
        noteDao.getAll()
            .map { l -> l.map { NotePreview.of(it) } }

    fun find(noteId: Long): Flow<NotePreview?> =
        noteDao.find(noteId)
            .map { note -> note?.let { NotePreview.of(it) } }

    suspend fun markCompleted(noteId: Long) =
        noteDao.markCompleted(noteId)

    class NoteViewModelFactory(
        private val noteDao: NoteDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NoteViewModel(noteDao) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }

    }
}