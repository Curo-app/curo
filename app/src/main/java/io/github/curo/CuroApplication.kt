package io.github.curo

import android.app.Application
import androidx.room.Room
import io.github.curo.database.AppDatabase

class CuroApplication : Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context = this,
            klass = AppDatabase::class.java,
            name = "curo_database"
        )
            .createFromAsset("databases/curo_database.db")
            .build()
    }
}
