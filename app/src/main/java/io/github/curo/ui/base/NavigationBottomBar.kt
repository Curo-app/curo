package io.github.curo.ui.base

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import io.github.curo.data.BottomMenu
import io.github.curo.ui.bottomMenu

@Composable
fun NavigationBottomBar(visible: Boolean, onItemSelected: (BottomMenu) -> Unit = {}) {
    if (!visible) {
        return
    }
    
    var selectedItem: BottomMenu by remember { mutableStateOf(BottomMenu.Feed) }
    NavigationBar {
        bottomMenu.forEach { item ->
            NavigationBarItem(
                selected = selectedItem == item,
                icon = { Icon(item.icon, contentDescription = stringResource(item.name)) },
                label = { Text(stringResource(item.name)) },
                onClick = {
                    selectedItem = item
                    onItemSelected(item)
                }
            )
        }
    }
}