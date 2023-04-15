package io.github.curo.ui.screens

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.CalendarLayoutInfo
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import io.github.curo.data.CalendarViewModel
import io.github.curo.data.CollectionName
import io.github.curo.ui.base.Calendar
import kotlinx.coroutines.flow.filterNotNull
import java.time.YearMonth
import java.util.*


@Composable
fun CalendarScreen(
    calendarViewModel: CalendarViewModel,
    calendarState: CalendarState,
    onCollectionClick: (CollectionName) -> Unit,
) {
    CalendarMenu(
        modifier = Modifier.fillMaxSize(),
        calendarState,
        calendarViewModel,
        onCollectionClick,
    )
}

@Composable
fun CalendarMenu(
    modifier: Modifier = Modifier,
    calendarState: CalendarState,
    calendarViewModel: CalendarViewModel,
    onCollectionClick: (CollectionName) -> Unit,
) {
    // TODO: make calendar move with borders
    Column(modifier = modifier, verticalArrangement = Arrangement.SpaceBetween) {
        CurrentCollections(
            onCollectionClick = onCollectionClick,
            viewModel = calendarViewModel
        )
        Calendar(
            modifier = Modifier.padding(bottom = 50.dp),
            state = calendarState,
            notesState = calendarViewModel.notes,
        )
    }
}

@Composable
private fun CurrentCollections(
    onCollectionClick: (CollectionName) -> Unit,
    viewModel: CalendarViewModel,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(viewModel.collectionsNames) { collection ->
            val currentItem by rememberUpdatedState(collection)
            CollectionChip(currentItem, onCollectionClick)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CollectionChip(
    current: CalendarViewModel.CollectionFilter,
    onCollectionClick: (CollectionName) -> Unit,
) {
    FilterChip(
        onClick = {
            current.enabled = !current.enabled
            onCollectionClick(current.name)
        },
        modifier = Modifier.padding(vertical = 0.dp),
        interactionSource = remember { MutableInteractionSource() },
        label = { Text(text = current.name.value) },
        selected = current.enabled,
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
    CalendarScreen(viewModel, rememberCuroCalendarState()) {}
}

@Composable
fun rememberCuroCalendarState(): CalendarState {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val daysOfWeek = remember { daysOfWeek() }
    return rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first(),
        outDateStyle = OutDateStyle.EndOfGrid,
    )
}