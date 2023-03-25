package io.github.curo.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.rounded.EventNote
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ViewHeadline
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.curo.R

@Immutable
sealed class Screen(
    val route: String,
)

@Immutable
sealed class MainScreen(
    route: String,
    val name: Int,
    val icon: ImageVector,
) : Screen(route) {
    @Immutable
    companion object : Screen("main")
}

@Immutable
sealed class SideMenu(
    route: String,
    name: Int,
    icon: ImageVector,
) : MainScreen(route, name, icon) {

    @Immutable
    object HomeScreen :
        SideMenu("feed", R.string.home_content_description, Icons.Rounded.Home)

    @Immutable
    object AboutUsScreen :
        SideMenu("about_us", R.string.about_us_screen_name, Icons.Rounded.Info)

    @Immutable
    object SettingsScreen :
        SideMenu("settings", R.string.settings_screen_name, Icons.Rounded.Settings)
}

@Immutable
sealed class BottomMenu(
    route: String,
    name: Int,
    icon: ImageVector,
) : MainScreen(route, name, icon) {

    @Immutable
    object Collections :
        BottomMenu("collections", R.string.collections_screen_name, Icons.Rounded.Folder)

    @Immutable
    object Feed :
        BottomMenu("feed", R.string.feed_screen_name, Icons.Rounded.ViewHeadline)

    @Immutable
    object Calendar :
        BottomMenu("calendar", R.string.calendar_screen_name, Icons.Rounded.EventNote)

}

@Immutable
sealed class FABMenu(
    route: String,
    name: Int,
    icon: ImageVector,
) : MainScreen(route, name, icon) {

    @Immutable
    object ShoppingList :
        FABMenu("create_shopping_list", R.string.shopping_list, Icons.Filled.ShoppingCart)

    @Immutable
    object TODOList :
        FABMenu("create_todo_list", R.string.todolist, Icons.Filled.Menu)

    @Immutable
    object Note :
        FABMenu("create_note", R.string.note, Icons.Filled.Edit)
}

@Immutable
object NoteOptionsScreen : Screen("note_options")

