package io.github.curo.viewmodels

import androidx.lifecycle.ViewModel
import io.github.curo.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel : ViewModel() {
    private val _themeMode = MutableStateFlow<ThemeMode>(ThemeMode.Companion.System)
    val themeMode: StateFlow<ThemeMode> = _themeMode

    fun onThemeChanged(newTheme: ThemeMode) {
        _themeMode.value = newTheme
    }
}


sealed interface ThemeMode {
    val resourceId: Int

    companion object {
        object System : ThemeMode {
            override val resourceId: Int = R.string.system_theme
        }

        object Light : ThemeMode {
            override val resourceId: Int = R.string.light_theme
        }

        object Dark : ThemeMode {
            override val resourceId: Int = R.string.dark_theme
        }
    }
}