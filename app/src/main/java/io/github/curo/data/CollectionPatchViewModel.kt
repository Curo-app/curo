package io.github.curo.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class CollectionPatchViewModel : FeedViewModel() {
    var name: String by mutableStateOf("")
    override val items: MutableList<Note> = mutableStateListOf()

    fun set(value: CollectionName) {
        if (value.name == this.name) return
        this.name = value.name
        this.items.clear()
        this.items.addAll(
            super.items.filter { item -> value.name in item.collections.map { it.name } }
        )
    }

    fun toCollection() = CollectionPreviewModel(
        name = name,
        notes = items,
    )
}
