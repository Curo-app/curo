package io.github.curo.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["noteId", "collectionName"])
data class NoteCollectionCrossRef(
    @ColumnInfo(index = true) val noteId: Long,
    @ColumnInfo(index = true) val collectionName: String
)
