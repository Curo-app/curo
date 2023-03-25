package io.github.curo.ui.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.curo.R
import io.github.curo.data.MainScreen
import io.github.curo.data.SideMenu

@Composable
fun SideMenu(onItemClick: (MainScreen) -> Unit) {
    NavigationMenuHeader()
    NavigationMenuBody(onItemClick = onItemClick)
}

@Preview(showBackground = true)
@Composable
fun NavigationMenuPreview() {
    SideMenu {}
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

val sideMenu = listOf(SideMenu.HomeScreen, SideMenu.SettingsScreen, SideMenu.AboutUsScreen)

@Composable
fun NavigationMenuBody(
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp),
    onItemClick: (MainScreen) -> Unit
) {
    Column(modifier) {
        sideMenu.forEach {
            SideMenuItem(it, onItemClick, itemTextStyle)
        }
    }
}

@Composable
private fun SideMenuItem(
    screen: MainScreen,
    onItemClick: (MainScreen) -> Unit,
    itemTextStyle: TextStyle
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClick(screen)
            }
            .padding(16.dp)
    ) {
        Icon(
            imageVector = screen.icon,
            contentDescription = stringResource(screen.name),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(screen.name),
            style = itemTextStyle,
            modifier = Modifier.weight(1f),
        )
    }
}