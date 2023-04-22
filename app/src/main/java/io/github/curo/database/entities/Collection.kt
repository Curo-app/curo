package io.github.curo.database.entities

import androidx.room.*

@Entity
data class Collection(
    @PrimaryKey val collectionName: String,
    val emoji: String = "\uD83D\uDDC2"
)

data class CollectionWithNotes(
    @Embedded val collection: Collection,
    @Relation(
        parentColumn = "collectionName",
        entityColumn = "noteId",
        associateBy = Junction(NoteCollectionCrossRef::class)
    )
    val notes: List<Note>
)
