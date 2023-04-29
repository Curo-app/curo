package io.github.curo.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.curo.database.converter.Converters
import io.github.curo.database.dao.CollectionDao
import io.github.curo.database.dao.NoteCollectionCrossRefDao
import io.github.curo.database.entities.Note
import io.github.curo.database.dao.NoteDao
import io.github.curo.database.entities.Collection
import io.github.curo.database.entities.NoteCollectionCrossRef

@Database(entities = [Note::class, Collection::class, NoteCollectionCrossRef::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun collectionDao(): CollectionDao
    abstract fun noteCollectionCrossRefDao(): NoteCollectionCrossRefDao
}