package io.github.curo.ui.screens

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.CalendarLayoutInfo
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import io.github.curo.R
import io.github.curo.data.CalendarViewModel
import io.github.curo.ui.base.Calendar
import kotlinx.coroutines.flow.filterNotNull
import java.time.YearMonth
import java.util.*


@Composable
fun CalendarScreen(
    calendarViewModel: CalendarViewModel
) {
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

    // TODO: move CalendarScreenTopAppBar
    //    Scaffold(
    //        topBar = {
    //            CalendarScreenTopAppBar(
    //                state,
    //                onNavigationIconClick = {},
    //                onSearchIconClick = {}
    //            )
    //        }
    //    ) { innerPadding ->
    //        CalendarMenu(
    //            modifier = Modifier.padding(innerPadding),
    //            state,
    //        )
    //    }

    CalendarMenu(
        modifier = Modifier.fillMaxSize(),
        state,
        calendarViewModel,
    )
}

@Composable
fun CalendarMenu(
    modifier: Modifier = Modifier,
    calendarState: CalendarState,
    calendarViewModel: CalendarViewModel,
) {
    // Background theme color
    Box(
        modifier = modifier
            .padding(top = 12.dp)
            .background(Color.LightGray)
    )

    val notesState by calendarViewModel.notes.collectAsState()

    // TODO: make calendar move with borders
    Calendar(
        modifier = modifier
            .background(Color.Transparent)  // in case first box color will be visible
            .padding(bottom = 50.dp)        // to show tags collection
            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
        calendarState,
        notesState
    )

    // TODO: think about how to get the names of the collection
    val collectionsNames =
        notesState
            .flatMap { it.collections }
            .distinct()
    // LazyColumn of Tags Collection
    LazyRow(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        items(collectionsNames) {
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                text = it.name
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreenTopAppBar(
    calendarState: CalendarState,
    onNavigationIconClick: () -> Unit,
    onSearchIconClick: () -> Unit,
) {
    val visibleMonth = rememberFirstMostVisibleMonth(calendarState, viewportPercent = 90f)

    val monthName = visibleMonth.yearMonth.month.name.lowercase().capitalizeFirstLetter()

    val year = visibleMonth.yearMonth.year

    TopAppBar(
        title = { Text(text = "$monthName $year") },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = stringResource(R.string.calendar_menu),
                )
            }
        },
        actions = {
            IconButton(onClick = onSearchIconClick) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = stringResource(R.string.calendar_search),
                )
            }
        }
    )
}

fun String.capitalizeFirstLetter(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else "$it"
    }
}

@Composable
fun rememberFirstMostVisibleMonth(
    state: CalendarState,
    viewportPercent: Float = 50f,
): CalendarMonth {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth) }
    LaunchedEffect(state) {
        snapshotFlow { state.layoutInfo.firstMostVisibleMonth(viewportPercent) }
            .filterNotNull()
            .collect { month -> visibleMonth.value = month }
    }
    return visibleMonth.value
}

private fun CalendarLayoutInfo.firstMostVisibleMonth(viewportPercent: Float = 50f): CalendarMonth? {
    return if (visibleMonthsInfo.isEmpty()) {
        null
    } else {
        val viewportSize = (viewportEndOffset + viewportStartOffset) * viewportPercent / 100f
        visibleMonthsInfo.firstOrNull { itemInfo ->
            if (itemInfo.offset < 0) {
                itemInfo.offset + itemInfo.size >= viewportSize
            } else {
                itemInfo.size - itemInfo.offset >= viewportSize
            }
        }?.month
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarPreview() {
    val viewModel = remember { CalendarViewModel() }
    CalendarScreen(viewModel)
}
