package io.github.curo.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.curo.R

@Immutable
sealed class BottomBarScreen(
    val route: Int,
    val name: Int,
    val icon: ImageVector,
) {
    @Immutable
    object Home : BottomBarScreen(R.string.home_route, R.string.home_screen_name, Icons.Rounded.Home)
    @Immutable
    object Create : BottomBarScreen(R.string.add_route, R.string.add_screen_name, Icons.Rounded.AddCircle)
    @Immutable
    object Settings : BottomBarScreen(R.string.settings_route, R.string.settings_screen_name, Icons.Rounded.Settings)

    @Immutable
    companion object {
        val items = listOf(Home, Create, Settings)
    }
}