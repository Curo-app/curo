package io.github.curo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.curo.database.converter.Converters
import io.github.curo.database.dao.CollectionDao
import io.github.curo.database.entities.Note
import io.github.curo.database.dao.NoteDao
import io.github.curo.database.entities.Collection
import io.github.curo.database.entities.NoteCollectionCrossRef

@Database(entities = [Note::class, Collection::class, NoteCollectionCrossRef::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun collectionDao(): CollectionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context = context,
                    klass = AppDatabase::class.java,
                    name = "app_database"
                )
                    .createFromAsset("databases/app_database.db")
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}