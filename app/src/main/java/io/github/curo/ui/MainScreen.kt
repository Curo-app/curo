package io.github.curo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.curo.data.BottomBarScreen
import io.github.curo.ui.base.SearchBar
import io.github.curo.ui.screens.HomeScreen

@Composable
fun SearchTopAppBar(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchBar(
            modifier = Modifier
                .weight(1f),
            onSearch = onSearch
        )
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = "Settings",
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(/* navController: NavHostController = rememberNavController() */) {
    var searchText by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf(0) }
    Scaffold(
        topBar = {
            // SearchBar is not yet implemented, so we need to make our own
            SearchTopAppBar(
                onSearch = { searchText = it }
            )
        },
        bottomBar = {
            // TODO: rewrite this using proper navigation
            NavigationBar {
                BottomBarScreen.items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        icon = { Icon(item.icon, contentDescription = item.name) },
                        label = { Text(item.name) },
                        onClick = {
                            selectedItem = index
                            // TODO: navController.navigate()
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // use HomeScreen() hardcoded for now
        HomeScreen(
            modifier = Modifier.padding(innerPadding)
        )
    }
}
