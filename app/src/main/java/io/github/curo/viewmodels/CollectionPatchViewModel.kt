package io.github.curo.viewmodels

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.curo.data.CollectionPreview
import io.github.curo.data.NotePreview
import io.github.curo.utils.setAll

@Stable
class CollectionPatchViewModel : FeedViewModel() {
    var name: String by mutableStateOf("")
    override val notes: MutableList<NotePreview> = mutableStateListOf()

    fun set(name: String) {
        if (name == this.name) return
        this.name = name
        this.notes.setAll(
            super.notes.filter { item -> name in item.collections }
        )
    }

    fun toCollection() = CollectionPreview(
        name = name,
        notes = notes,
    )
}
