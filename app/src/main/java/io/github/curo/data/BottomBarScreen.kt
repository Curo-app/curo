package io.github.curo.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val name: String,
    val icon: ImageVector,
) {
    object Home : BottomBarScreen("home", "Home", Icons.Rounded.Home)
    object Create : BottomBarScreen("add", "Create", Icons.Rounded.AddCircle)
    object Settings : BottomBarScreen("settings", "Settings", Icons.Rounded.Settings)

    companion object {
        val items = listOf(Home, Create, Settings)

        fun fromRoute(route: String): BottomBarScreen = when (route) {
            Home.route -> Home
            Create.route -> Create
            Settings.route -> Settings
            else -> Home
        }
    }
}
