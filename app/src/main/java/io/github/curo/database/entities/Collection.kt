package io.github.curo.database.entities

import androidx.room.*
import io.github.curo.data.CollectionPreview

@Entity
data class Collection(
    @PrimaryKey @ColumnInfo(name = "collection_id") val collectionId: Long,
    @ColumnInfo(name = "collection_name") val collectionName: String,
    val emoji: String = "\uD83D\uDDC2"
) {
    companion object {
        fun of(collectionPreview: CollectionPreview): Collection =
            Collection(
                collectionId = collectionPreview.id,
                collectionName = collectionPreview.name,
                emoji = collectionPreview.emoji.value
            )
    }
}

data class CollectionWithNotes(
    @Embedded val collection: Collection,
    @Relation(
        parentColumn = "collection_id",
        entityColumn = "note_id",
        associateBy = Junction(NoteCollectionCrossRef::class)
    )
    val notes: List<Note>
)
