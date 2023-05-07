package io.github.curo.database.dao

import androidx.room.*
import io.github.curo.database.entities.Collection
import io.github.curo.database.entities.CollectionWithNotes
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(collection: Collection): Long

    @Query("DELETE FROM Collection WHERE collection_id = :collectionId")
    suspend fun delete(collectionId: Long)

    @Update
    suspend fun update(collection: Collection)

    @Transaction
    @Query("SELECT * FROM Collection")
    fun getAll(): Flow<List<CollectionWithNotes>>

    @Transaction
    @Query("SELECT * FROM Collection WHERE collection_id = :collectionId")
    fun find(collectionId: Long): Flow<CollectionWithNotes?>
}