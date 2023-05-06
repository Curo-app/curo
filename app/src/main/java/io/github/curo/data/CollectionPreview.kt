package io.github.curo.data

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import io.github.curo.database.entities.CollectionWithNotes

@Stable
class CollectionPreview(
    val id: Long = 0,
    val emoji: Emoji = Emoji("\uD83D\uDDC2"),
    val name: String,
    val notes: List<NotePreview>
) {
    val progress: CollectionProgress? by derivedStateOf {
        notes.let { notes ->
            val size = notes.size
            val todoNotes = notes.count { it.done != null }

            if (todoNotes == 0) null
            else CollectionProgress(
                total = size,
                done = size - notes.count { it.done == false }
            )
        }
    }

    companion object {
        fun of(collectionWithNotes: CollectionWithNotes): CollectionPreview =
            CollectionPreview(
                emoji = Emoji(collectionWithNotes.collection.emoji),
                name = collectionWithNotes.collection.collectionName,
                notes = collectionWithNotes.notes.map { NotePreview.of(it) }
            )
    }

    override fun toString(): String {
        return "CollectionPreviewModel(emoji=$emoji, name='$name', notes=$notes, progress=$progress)"
    }
}

@Immutable
data class CollectionProgress(val total: Int, val done: Int) {
    override fun toString(): String {
        return "$done/$total"
    }

    fun isFinished(): Boolean = total == done
}

