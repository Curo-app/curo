package io.github.curo.database.entities

import androidx.room.*
import io.github.curo.data.CollectionPreviewModel

@Entity
data class Collection(
    @PrimaryKey @ColumnInfo(name = "collection_name") val collectionName: String,
    val emoji: String = "\uD83D\uDDC2"
) {
    companion object {
        fun of(collectionPreview: CollectionPreviewModel): Collection =
            Collection(
                collectionName = collectionPreview.name,
                emoji = collectionPreview.emoji.value
            )
    }
}

data class CollectionWithNotes(
    @Embedded val collection: Collection,
    @Relation(
        parentColumn = "collection_name",
        entityColumn = "note_id",
        associateBy = Junction(NoteCollectionCrossRef::class)
    )
    val notes: List<Note>
)
