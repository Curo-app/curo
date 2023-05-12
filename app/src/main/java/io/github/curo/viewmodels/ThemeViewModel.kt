package io.github.curo.viewmodels

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.curo.R
import kotlinx.coroutines.flow.*

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val _themeMode: Flow<ThemeMode> =
        getApplication<Application>().applicationContext.dataStorage
            .data
            .map { preferences ->
                ThemeMode.of(preferences[THEME_KEY] ?: "")
            }

    val themeMode: StateFlow<ThemeMode> = _themeMode.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeMode.Companion.System
    )

    suspend fun changeTheme(newTheme: ThemeMode) {
        getApplication<Application>().applicationContext.dataStorage
            .edit { preferences ->
                preferences[THEME_KEY] = newTheme.name
            }
    }

    companion object {
        private val Context.dataStorage: DataStore<Preferences> by
        preferencesDataStore("Theme")

        val THEME_KEY = stringPreferencesKey("theme")
    }
}


sealed interface ThemeMode {
    val resourceId: Int
    val name: String

    companion object {
        object System : ThemeMode {
            override val resourceId: Int = R.string.system_theme
            override val name: String = "System"
        }

        object Light : ThemeMode {
            override val resourceId: Int = R.string.light_theme
            override val name: String = "Light"
        }

        object Dark : ThemeMode {
            override val resourceId: Int = R.string.dark_theme
            override val name: String = "Dark"
        }

        fun of(name: String) = when (name) {
            "Light" -> Light
            "Dark" -> Dark
            else -> System
        }
    }
}