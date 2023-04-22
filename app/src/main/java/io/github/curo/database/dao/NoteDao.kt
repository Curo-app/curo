package io.github.curo.database.dao

import androidx.room.*
import io.github.curo.database.entities.Note
import io.github.curo.database.entities.NoteWithCollectionNames
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Transaction
    @Query("SELECT * FROM Note")
    fun getAll(): Flow<List<NoteWithCollectionNames>>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(noteWithCollectionNames: Note)
}