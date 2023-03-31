package io.github.curo.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.EventNote
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ViewHeadline
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.curo.R

interface Route {
    val route: String
}

@Immutable
sealed interface MenuItem : Route {
    val menuName: Int
    val menuIcon: ImageVector
}

@Immutable
sealed interface FABMenuItem : MenuItem

@Immutable
sealed interface SideMenuItem : MenuItem

@Immutable
sealed class Screen(override val route: String) : Route {

    @Immutable
    object AboutUs : Screen("about_us"), SideMenuItem {
        override val menuName = R.string.about_us_screen_name
        override val menuIcon = Icons.Rounded.Info
    }

    @Immutable
    object Settings : Screen("settings"), SideMenuItem {
        override val menuName = R.string.settings_screen_name
        override val menuIcon = Icons.Rounded.Settings
    }

    @Immutable
    object NoteOptions : Screen("note_options")

    @Immutable
    object SearchResult : Screen("search_result")

    @Immutable
    object EditCollection : Screen("edit_collection"), FABMenuItem {
        override val menuName = R.string.collection
        override val menuIcon = Icons.Filled.Menu
    }

    @Immutable
    object EditNote : Screen("edit_note"), FABMenuItem {
        override val menuName = R.string.note
        override val menuIcon = Icons.Filled.Edit
    }
}

@Immutable
sealed class BottomNavigationScreen(
    override val route: String,
    override val menuName: Int,
    override val menuIcon: ImageVector,
) : MenuItem {
    companion object : Screen("bottom_navigation"), SideMenuItem {
        override val menuName = R.string.home_content_description
        override val menuIcon = Icons.Rounded.Home
    }

    @Immutable
    object Collections : BottomNavigationScreen(
        "collections",
        R.string.collections_screen_name,
        Icons.Rounded.Folder,
    )

    @Immutable
    object Feed : BottomNavigationScreen(
        "feed",
        R.string.feed_screen_name,
        Icons.Rounded.ViewHeadline
    )

    @Immutable
    object Calendar : BottomNavigationScreen(
        "calendar",
        R.string.calendar_screen_name,
        Icons.Rounded.EventNote,
    )
}
