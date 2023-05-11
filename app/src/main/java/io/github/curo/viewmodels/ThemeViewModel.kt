package io.github.curo.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel : ViewModel() {
    private val _themeState = MutableStateFlow(ThemeState())
    val themeState: StateFlow<ThemeState> = _themeState

    fun onThemeChanged(newThemeIsDark: Boolean) {
        _themeState.value = ThemeState(newThemeIsDark, initialized = true)
    }
}

data class ThemeState(val darkTheme: Boolean = false, val initialized: Boolean = false)