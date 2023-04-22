package io.github.curo.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.curo.database.entities.Note
import io.github.curo.database.entities.NoteWithCollectionNames

@Stable
class NotePreviewModel(
    val id: Long = 0,
    val deadline: Deadline? = null,
    val emoji: Emoji = Emoji("\uD83D\uDCDD"),
    val name: String,
    val description: String? = null,
    val collections: List<String> = emptyList(),
    done: Boolean? = null
) {
    var done by mutableStateOf(done)

    companion object {
        fun Collection<NotePreviewModel>.extractCollections(): List<String> =
            this.flatMap { it.collections }.distinct()

        fun of(noteWithCollectionNames: NoteWithCollectionNames): NotePreviewModel {
            val note = noteWithCollectionNames.note
            val deadline = Deadline.of(note.deadlineDate, note.deadlineTime)

            return NotePreviewModel(
                id = note.noteId,
                deadline = deadline,
                emoji = Emoji(note.emoji),
                name = note.name,
                description = note.description,
                collections = noteWithCollectionNames.collections,
                done = note.done
            )
        }

        fun of(note: Note): NotePreviewModel = of(NoteWithCollectionNames(note, listOf()))
    }

    override fun toString(): String {
        return "NotePreviewModel(id=$id, deadline=$deadline, emoji=$emoji, name='$name', description=$description, collections=$collections, done=$done)"
    }
}
