package io.github.curo.ui.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.curo.R
import io.github.curo.data.BottomNavigationScreen
import io.github.curo.data.Screen
import io.github.curo.data.SideMenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SideMenu(
    drawerState: DrawerState,
    selected: String?,
    onItemClick: (SideMenuItem) -> Unit,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationMenuHeader()
                Spacer(modifier = Modifier.width(16.dp))
                NavigationMenuBody(
                    onItemClick = onItemClick,
                    selected = selected
                )
            }
        },
        gesturesEnabled = drawerState.isOpen,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun NavigationMenuPreview() {
    SideMenu(
        drawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
        onItemClick = {},
        content = {},
        selected = null,
    )
}

@Composable
fun NavigationMenuHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(id = R.string.app_name), fontSize = 30.sp)
    }
}

val sideMenu: List<SideMenuItem> = listOf(BottomNavigationScreen, Screen.Settings, Screen.AboutUs)

@Composable
fun NavigationMenuBody(
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp),
    onItemClick: (SideMenuItem) -> Unit,
    selected: String?,
) {
    Column(modifier) {
        sideMenu.forEach {
            SideMenuItem(it, onItemClick, itemTextStyle, selected)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SideMenuItem(
    screen: SideMenuItem,
    onItemClick: (SideMenuItem) -> Unit,
    itemTextStyle: TextStyle,
    selected: String?,
) {
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = screen.menuIcon,
                contentDescription = stringResource(screen.menuName),
            )
        },
        label = {
            Text(
                text = stringResource(screen.menuName),
                style = itemTextStyle,
            )
        },
        onClick = { onItemClick(screen) },
        selected = screen.route == selected,
    )
}