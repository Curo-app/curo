package io.github.curo.database.entities

import androidx.room.*
import io.github.curo.data.NotePreviewModel
import io.github.curo.data.TimedDeadline
import java.time.LocalDate
import java.time.LocalTime

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "note_id") val noteId: Long,
    val name: String,
    @ColumnInfo(name = "deadline_date") val deadlineDate: LocalDate?,
    @ColumnInfo(name = "deadline_time") val deadlineTime: LocalTime?,
    val emoji: String,
    val description: String?,
    val done: Boolean?
) {
    companion object {
        fun of(notePreview: NotePreviewModel): Note =
            Note(
                noteId = notePreview.id,
                name = notePreview.name,
                deadlineDate = notePreview.deadline?.date,
                deadlineTime = when (notePreview.deadline) {
                    is TimedDeadline -> notePreview.deadline.time
                    else -> null
                },
                emoji = notePreview.emoji.value,
                description = notePreview.description,
                done = notePreview.done
            )
    }
}

data class NoteWithCollectionNames(
    @Embedded val note: Note,
    @Relation(
        parentColumn = "note_id",
        entityColumn = "collection_name",
        associateBy = Junction(NoteCollectionCrossRef::class),
        entity = Collection::class,
        projection = ["collection_name"]
    )
    val collections: List<String>,
)
