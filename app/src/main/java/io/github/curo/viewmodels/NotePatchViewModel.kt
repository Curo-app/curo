package io.github.curo.viewmodels

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Transaction
import io.github.curo.data.Deadline
import io.github.curo.data.NotePreview
import io.github.curo.database.dao.NoteCollectionCrossRefDao
import io.github.curo.database.dao.NoteDao
import io.github.curo.database.entities.Note
import io.github.curo.database.entities.NoteCollectionCrossRef
import io.github.curo.utils.setAll

@Stable
class NotePatchViewModel(
    private val noteDao: NoteDao,
    private val noteCollectionCrossRefDao: NoteCollectionCrossRefDao
) : ViewModel() {
    var id: Long by mutableStateOf(0L)
        private set

    var name: String by mutableStateOf("")
    var description: String by mutableStateOf("")

    var deadline: Deadline? by mutableStateOf(null)
    var newCollection: String? by mutableStateOf(null)
    var collections = mutableStateListOf<String>()

    var hasCheckbox by mutableStateOf(false)

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

    /*
    * Sets the values of the view model to the default values.
     */
    fun clear() {
        id = 0
        name = ""
        description = ""
        deadline = null
        newCollection = null
        collections.clear()
        hasCheckbox = false
    }

    /*
    * Sets the values of the view model to the values of the given note.
    * If the note has the same id as the current note, nothing happens.
     */
    fun set(note: NotePreview) {
        if (note.id == id) {
            return
        }
        id = note.id
        name = note.name
        description = note.description ?: ""
        collections.setAll(note.collections)
        deadline = note.deadline
        hasCheckbox = note.done != null
    }


    fun toNote() = NotePreview(
        id = id,
        name = name,
        description = description,
        deadline = deadline,
        collections = collections,
        done = if (hasCheckbox) false else null
    )

    suspend fun saveNote() {
        val notePreview = toNote()
        if (notePreview.id == 0L) {
            insert(notePreview)
        } else {
            update(notePreview)
        }
    }

    class NotePatchViewModelFactory(
        private val noteDao: NoteDao,
        private val noteCollectionCrossRefDao: NoteCollectionCrossRefDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NotePatchViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NotePatchViewModel(noteDao, noteCollectionCrossRefDao) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }
}
