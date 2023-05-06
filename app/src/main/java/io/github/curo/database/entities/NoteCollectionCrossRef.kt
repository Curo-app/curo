package io.github.curo.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    primaryKeys = ["note_id", "collection_id"],
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["note_id"],
            childColumns = ["note_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = Collection::class,
            parentColumns = ["collection_id"],
            childColumns = ["collection_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class NoteCollectionCrossRef(
    @ColumnInfo(name = "note_id", index = true) val noteId: Long,
    @ColumnInfo(name = "collection_id", index = true) val collectionId: Long
)
