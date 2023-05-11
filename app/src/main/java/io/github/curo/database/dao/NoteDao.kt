package io.github.curo.database.dao

import androidx.room.*
import io.github.curo.database.entities.Note
import io.github.curo.database.entities.NoteWithCollections
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<Note>): List<Long>

    @Update
    suspend fun update(note: Note)

    @Update
    suspend fun updateAll(notes: List<Note>)

    @Query("DELETE FROM Note WHERE note_id = :noteId")
    suspend fun delete(noteId: Long)

    @Transaction
    @Query("DELETE FROM Note WHERE note_id IN (:noteIds)")
    suspend fun deleteAll(noteIds: List<Long>)

    @Transaction
    @Query("SELECT * FROM Note")
    fun getAll(): Flow<List<NoteWithCollections>>

    @Transaction
    @Query("SELECT * FROM Note WHERE note_id = :noteId")
    fun find(noteId: Long): Flow<NoteWithCollections?>

    @Transaction
    @Query("SELECT * FROM Note WHERE note_id IN (:noteIds)")
    fun findAll(noteIds: List<Long>): Flow<List<NoteWithCollections>>

    @Query("UPDATE Note SET done = TRUE WHERE note_id = :noteId")
    suspend fun markCompleted(noteId: Long)

    @Transaction
    @Query("SELECT * FROM Note WHERE deadline_date = :date")
    fun getNotesForDate(date: LocalDate): Flow<List<NoteWithCollections>>
}