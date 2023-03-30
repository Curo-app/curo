package io.github.curo.ui.base

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.curo.data.BottomNavigationScreen
import io.github.curo.ui.bottomMenu

@Composable
fun NavigationBottomBar(
    selected: String?,
    onItemSelected: (BottomNavigationScreen) -> Unit = {}
) {
    NavigationBar {
        bottomMenu.forEach { item ->
            NavigationBarItem(
                selected = selected == item.route,
                icon = { Icon(item.menuIcon, contentDescription = stringResource(item.menuName)) },
                label = { Text(stringResource(item.menuName)) },
                onClick = { onItemSelected(item) },
            )
        }
    }
}