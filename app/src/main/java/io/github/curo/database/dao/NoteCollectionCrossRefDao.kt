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

    @Query("SELECT * FROM NoteCollectionCrossRef WHERE collection_name = :collectionName")
    fun listByCollectionName(collectionName: String): Flow<List<NoteCollectionCrossRef>>
}