package io.github.curo.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.curo.database.entities.NoteCollectionCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteCollectionCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(crossRefs: List<NoteCollectionCrossRef>)

    @Query("DELETE FROM NoteCollectionCrossRef WHERE note_id = :noteId")
    suspend fun deleteAllByNoteId(noteId: Long)

    @Query("SELECT * FROM NoteCollectionCrossRef WHERE collection_id = :collectionId")
    fun listByCollectionId(collectionId: Long): Flow<List<NoteCollectionCrossRef>>
}