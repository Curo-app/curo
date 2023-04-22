package io.github.curo

import android.app.Application
import io.github.curo.database.AppDatabase

class CuroApplication : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}
