package io.github.curo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import io.github.curo.R
import io.github.curo.data.BottomBarScreen
import io.github.curo.ui.base.SearchBar
import io.github.curo.ui.screens.CalendarMenu
import io.github.curo.ui.screens.CalendarMenuTopAppBar
import java.time.YearMonth

@Composable
fun SearchTopAppBar(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {},
    onSettingsClick: () -> Unit = {},
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
        IconButton(onClick = onSettingsClick) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = stringResource(R.string.topappbar_settings_icon_description),
            )
        }
    }
}

@Composable
private fun NavigationBottomBar(onItemSelected: (Int) -> Unit = {}) {
    var selectedItem by remember { mutableStateOf(2) }
    NavigationBar {
        BottomBarScreen.items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                icon = { Icon(item.icon, contentDescription = stringResource(item.name)) },
                label = { Text(stringResource(item.name)) },
                onClick = {
                    selectedItem = index
                    onItemSelected(index)
                    // TODO: navController.navigate()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(/* navController: NavHostController = rememberNavController() */) {
    var searchText by remember { mutableStateOf("") }

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val daysOfWeek = remember { daysOfWeek() }
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first(),
        outDateStyle = OutDateStyle.EndOfGrid,
    )

    Scaffold(
        topBar = {
            CalendarMenuTopAppBar(
                state,
                onNavigationIconClick = { /* TODO */ },
                onSearchIconClick = { /* TODO */ }
            )
        }
    ) { innerPadding ->
        // use HomeScreen() hardcoded for now
        CalendarMenu(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            state,
        )
    }
}