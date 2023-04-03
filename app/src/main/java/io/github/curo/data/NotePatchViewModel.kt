package io.github.curo.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

@Stable
class NotePatchViewModel : ViewModel() {
    private var _id: Int by mutableStateOf(-1)
    val id get() = _id
    var name: String by mutableStateOf("My Note")
    var description: String by mutableStateOf("")

    var deadline: Deadline? by mutableStateOf(null)
    var newCollection: CollectionName? by mutableStateOf(null)
    var collections = mutableStateListOf<CollectionName>()

    var hasCheckbox by mutableStateOf(false)

    fun set(note: Note) {
        if (note.id == _id) {
            return
        }
        _id = note.id
        name = note.name
        description = note.description ?: ""
        collections.clear()
        collections.addAll(note.collections)
        deadline = note.deadline
        hasCheckbox = note.done != null
    }

    fun empty(id: Int) {
        if (id == _id) {
            return
        }
        _id = id
        name = "My Note"
        collections.clear()
        deadline = null
        hasCheckbox = false
    }

    fun toNote() = Note(
        id = id,
        name = name,
        description = description,
        deadline = deadline,
        collections = collections,
        done = if (hasCheckbox) false else null
    )
}

