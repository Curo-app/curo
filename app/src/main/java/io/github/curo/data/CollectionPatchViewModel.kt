package io.github.curo.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.curo.utils.setAll

@Stable
class CollectionPatchViewModel : FeedViewModel() {
    var name: String by mutableStateOf("")
    override val notes: MutableList<Note> = mutableStateListOf()

    fun set(name: String) {
        if (name == this.name) return
        this.name = name
        this.notes.setAll(
            super.notes.filter { item -> name in item.collections }
        )
    }

    fun toCollection() = CollectionPreviewModel(
        name = name,
        notes = notes,
    )
}
