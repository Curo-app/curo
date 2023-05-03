package io.github.curo.viewmodels

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.curo.data.Deadline
import io.github.curo.data.NotePreview
import io.github.curo.database.dao.NoteDao
import io.github.curo.database.entities.Note
import io.github.curo.utils.setAll

@Stable
class NotePatchViewModel(
    private val noteDao: NoteDao,
) : ViewModel() {
    var id: Long by mutableStateOf(0L)
        private set

    var name: String by mutableStateOf("")
    var description: String by mutableStateOf("")

    var deadline: Deadline? by mutableStateOf(null)
    var newCollection: String? by mutableStateOf(null)
    var collections = mutableStateListOf<String>()

    var hasCheckbox by mutableStateOf(false)

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
            noteDao.insert(Note.of(notePreview))
        } else {
            noteDao.update(Note.of(notePreview))
        }
    }

    class NotePatchViewModelFactory(
        private val noteDao: NoteDao,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NotePatchViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NotePatchViewModel(noteDao) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }
}
