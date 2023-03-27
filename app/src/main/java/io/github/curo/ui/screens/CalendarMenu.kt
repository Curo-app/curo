package io.github.curo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.CalendarLayoutInfo
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.core.CalendarMonth
import io.github.curo.R
import io.github.curo.ui.base.Calendar
import kotlinx.coroutines.flow.filterNotNull
import java.util.*

@Composable
fun CalendarMenu(
    modifier: Modifier = Modifier,
    calendarState: CalendarState,
) {
    // Background theme color
    Box(
        modifier = modifier
            .padding(top = 12.dp)
            .background(Color.LightGray)
    )

    // TODO: make calendar move with borders
    Calendar(
        modifier = modifier
            .background(Color.Transparent)  // in case first box color will be visible
            .padding(bottom = 50.dp)        // to show tags collection
            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
        calendarState
    )

    // LazyColumn of Tags Collection
    LazyRow(
        modifier = modifier
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        items(10) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                text = "Tag $it"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarMenuTopAppBar(
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