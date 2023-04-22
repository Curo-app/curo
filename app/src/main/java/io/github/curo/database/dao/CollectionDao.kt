package io.github.curo.database.dao

import androidx.room.*
import io.github.curo.database.entities.Collection
import io.github.curo.database.entities.CollectionWithNotes
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(collection: Collection)

    @Query("DELETE FROM Collection WHERE collection_name = :collectionName")
    suspend fun delete(collectionName: String)

    @Update
    suspend fun update(collection: Collection)

    @Transaction
    @Query("SELECT * FROM Collection")
    fun getAll(): Flow<List<CollectionWithNotes>>

    @Transaction
    @Query("SELECT * FROM Collection WHERE collection_name = :collectionName")
    fun find(collectionName: String): Flow<CollectionWithNotes?>
}