package io.github.curo.data

import androidx.compose.runtime.Immutable

@Immutable
data class CollectionPreviewModel(
    val id: Int,
    val emoji: Emoji = Emoji("\uD83D\uDDC2"),
    val name: String,
    val notes: List<NotePreviewModel>
) {
    val progress: CollectionProgress? by lazy {
        val size = notes.size
        val todoNotes = notes.filter { it.done != null }

        if (todoNotes.isEmpty()) null
        else CollectionProgress(total = size, done = size - todoNotes.count { it.done == false })
    }
}

@Immutable
data class CollectionProgress(val total: Int, val done: Int) {
    override fun toString(): String {
        return "$done/$total"
    }

    fun isFinished(): Boolean = total== done
}

