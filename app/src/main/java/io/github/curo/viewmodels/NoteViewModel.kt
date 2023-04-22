package io.github.curo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.curo.data.NotePreviewModel
import io.github.curo.database.entities.Note
import io.github.curo.database.dao.NoteDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteViewModel(private val noteDao: NoteDao) : ViewModel() {
    fun getAll(): Flow<List<NotePreviewModel>> =
        noteDao.getAll()
            .map { l -> l.map { NotePreviewModel.of(it) } }

    suspend fun insert(notePreview: NotePreviewModel) =
        noteDao.insert(Note.of(notePreview))

    class NoteViewModelFactory(private val noteDao: NoteDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NoteViewModel(noteDao) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }

    }
}