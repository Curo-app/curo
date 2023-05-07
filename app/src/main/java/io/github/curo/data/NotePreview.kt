package io.github.curo.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.curo.database.entities.CollectionInfo
import io.github.curo.database.entities.Note
import io.github.curo.database.entities.NoteWithCollections

@Stable
class NotePreview(
    val id: Long = 0,
    val deadline: Deadline? = null,
    val emoji: Emoji = Emoji("\uD83D\uDCDD"),
    val name: String,
    val description: String? = null,
    val collections: List<CollectionInfo> = emptyList(),
    done: Boolean? = null
) {
    var done by mutableStateOf(done)

    companion object {
        fun Collection<NotePreview>.extractCollections(): List<CollectionInfo> =
            this.flatMap { it.collections }.distinct()

        fun of(noteWithCollectionNames: NoteWithCollections): NotePreview {
            val note = noteWithCollectionNames.note
            val deadline = Deadline.of(note.deadlineDate, note.deadlineTime)

            return NotePreview(
                id = note.noteId,
                deadline = deadline,
                emoji = Emoji(note.emoji),
                name = note.name,
                description = note.description,
                collections = noteWithCollectionNames.collections,
                done = note.done
            )
        }

        fun of(note: Note): NotePreview = of(NoteWithCollections(note, listOf()))
    }

    override fun toString(): String {
        return "NotePreviewModel(id=$id, deadline=$deadline, emoji=$emoji, name='$name', description=$description, collections=$collections, done=$done)"
    }
}
