package io.github.curo.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.curo.utils.setAll

@Stable
class NotePatchViewModel : ViewModel() {
    private var _id: Int by mutableStateOf(-1)
    val id get() = _id
    var name: String by mutableStateOf("My Note")
    var description: String by mutableStateOf("")

    var deadline: Deadline? by mutableStateOf(null)
    var newCollection: String? by mutableStateOf(null)
    var collections = mutableStateListOf<String>()

    var hasCheckbox by mutableStateOf(false)

    fun set(note: Note) {
        if (note.id == _id) {
            return
        }
        _id = note.id
        name = note.name
        description = note.description ?: ""
        collections.setAll(note.collections)
        deadline = note.deadline
        hasCheckbox = note.done != null
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

