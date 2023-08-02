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
import io.github.curo.database.dao.CollectionDao
import io.github.curo.database.dao.NoteCollectionCrossRefDao
import io.github.curo.database.dao.NoteDao
import io.github.curo.database.entities.CollectionInfo
import io.github.curo.database.entities.Note
import io.github.curo.database.entities.NoteCollectionCrossRef
import io.github.curo.utils.setAll
import io.github.curo.database.entities.Collection as CollectionEntity

@Stable
class NotePatchViewModel(
    private val noteDao: NoteDao,
    private val collectionDao: CollectionDao,
    private val noteCollectionCrossRefDao: NoteCollectionCrossRefDao
) : ViewModel() {
    var id: Long by mutableStateOf(0L)
        private set

    var name: String by mutableStateOf("")
    var description: String by mutableStateOf("")

    var deadline: Deadline? by mutableStateOf(null)
    var newCollection: CollectionInfo? by mutableStateOf(null)
    var collections = mutableStateListOf<CollectionInfo>()

    var hasCheckbox by mutableStateOf(false)

    @Transaction
    suspend fun insert(notePreview: NotePreview) : Long {
        val noteId = noteDao.insert(Note.of(notePreview))
        val crossRefs = notePreview.collections
            .map { NoteCollectionCrossRef(noteId, it.collectionId) }
        noteCollectionCrossRefDao.insertAll(crossRefs)
        return noteId
    }

    suspend fun delete(noteId: Long) = noteDao.delete(noteId)

    @Transaction
    suspend fun update(notePreview: NotePreview) {
        noteDao.update(Note.of(notePreview))

        val createdInNoteOptionsMenu = notePreview.collections
            .filter { it.collectionId == 0L }
            .map { CollectionEntity.of(it) }

        val updatedInNoteOptionsMenu = notePreview.collections
            .filter { it.collectionId != 0L }
            .map { CollectionEntity.of(it) }

        val ids = collectionDao.insertAll(createdInNoteOptionsMenu)

        noteCollectionCrossRefDao.deleteAllByNoteId(notePreview.id)
        val crossRefs = updatedInNoteOptionsMenu
            .map { NoteCollectionCrossRef(notePreview.id, it.collectionId) }
        val insertedCrossRefs = ids
            .map { NoteCollectionCrossRef(notePreview.id, it) }

        noteCollectionCrossRefDao.insertAll(crossRefs)
        noteCollectionCrossRefDao.insertAll(insertedCrossRefs)
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

//    suspend fun saveNote() : Long {
//        val notePreview = toNote()
//        return if (notePreview.id == 0L) {
//            insert(notePreview)
//        } else {
//            update(notePreview)
//            notePreview.id
//        }
//    }

    suspend fun insertInCollection(collectionId: Long): Long {
        val noteId = insertNote()
        noteCollectionCrossRefDao.insert(
            NoteCollectionCrossRef(noteId, collectionId)
        )
        return noteId
    }

    suspend fun insertNote(): Long {
        val notePreview = toNote()
        return insert(notePreview)
    }

    suspend fun updateNote() {
        val notePreview = toNote()
        update(notePreview)
    }

    override fun toString(): String {
        return "NotePatchViewModel(id=$id, name='$name', description='$description', deadline=$deadline, newCollection=$newCollection, collections=$collections, hasCheckbox=$hasCheckbox)"
    }

//    fun isCreateInEditCollection() : Boolean {
//        return newCollection != null
//    }

    class NotePatchViewModelFactory(
        private val noteDao: NoteDao,
        private val collectionDao: CollectionDao,
        private val noteCollectionCrossRefDao: NoteCollectionCrossRefDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NotePatchViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NotePatchViewModel(noteDao, collectionDao, noteCollectionCrossRefDao) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }
}
