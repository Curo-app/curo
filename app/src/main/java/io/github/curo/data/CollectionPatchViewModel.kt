package io.github.curo.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.curo.data.CollectionName.Companion.extractNames
import io.github.curo.utils.setAll

@Stable
class CollectionPatchViewModel : FeedViewModel() {
    var name: String by mutableStateOf("")
    override val notes: MutableList<Note> = mutableStateListOf()

    fun set(name: CollectionName) {
        if (name.value == this.name) return
        this.name = name.value
        this.notes.setAll(
            super.notes.filter { item -> name.value in item.collections.extractNames() }
        )
    }

    fun toCollection() = CollectionPreviewModel(
        name = CollectionName(name),
        notes = notes,
    )
}
