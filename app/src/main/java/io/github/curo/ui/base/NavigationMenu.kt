package io.github.curo.ui.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.curo.R
import io.github.curo.data.MenuItem
import io.github.curo.ui.SearchTopAppBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavigationMenu(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    topBar: @Composable (CoroutineScope, ScaffoldState) -> Unit,
    content: @Composable (Modifier, PaddingValues) -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { topBar(scope, scaffoldState) },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerContent = {
            NavigationMenuHeader()
            NavigationMenuBody(
                items = listOf(
                    MenuItem(
                        title = stringResource(id = R.string.home_screen_name),
                        contentDescription = stringResource(id = R.string.home_content_description),
                        icon = Icons.Rounded.Home
                    ),
                    MenuItem(
                        title = stringResource(id = R.string.settings_screen_name),
                        contentDescription = stringResource(id = R.string.settings_content_description),
                        icon = Icons.Rounded.Settings
                    ),
                    MenuItem(
                        title = stringResource(id = R.string.about_us),
                        contentDescription = stringResource(id = R.string.about_us_content_description),
                        icon = Icons.Rounded.Info
                    ),
                ),
                onItemClick = {
                    println("Clicked on ${it.title}")
                }
            )
        }
    ) { innerPadding ->
        content(modifier, innerPadding)
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationMenuPreview() {
    NavigationMenu(
        scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Open)),
        topBar = { scope, scaffoldState ->
            SearchTopAppBar(
                onMenuClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            )
        },
        content = { modifier, innerPadding ->
            Feed(
                modifier = modifier.padding(innerPadding),
                onNoteClick = {},
                onCollectionClick = {},
                content = listOf()
            )
        })
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

@Composable
fun NavigationMenuBody(
    items: List<MenuItem>,
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp),
    onItemClick: (MenuItem) -> Unit
) {
    LazyColumn(modifier) {
        items(items) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onItemClick(item)
                    }
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.contentDescription
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.title,
                    style = itemTextStyle,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}