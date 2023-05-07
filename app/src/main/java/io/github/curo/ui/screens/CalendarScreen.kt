package io.github.curo.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import io.github.curo.database.entities.CollectionInfo
import io.github.curo.viewmodels.CalendarViewModel
import io.github.curo.ui.base.LandscapeCalendar
import io.github.curo.ui.base.PortraitCalendar
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.*

private val cellsBackgroundColor: Color @Composable get() = MaterialTheme.colorScheme.background

@Composable
fun CalendarScreen(
    calendarViewModel: CalendarViewModel,
    calendarState: CalendarState,
    onCollectionClick: (CollectionInfo) -> Unit,
    onDayClick: (LocalDate) -> Unit,
) {
    CalendarMenu(
        modifier = Modifier.fillMaxSize(),
        calendarState,
        calendarViewModel,
        onCollectionClick,
        onDayClick,
    )
}

@Composable
fun CalendarMenu(
    modifier: Modifier = Modifier,
    calendarState: CalendarState,
    calendarViewModel: CalendarViewModel,
    onCollectionClick: (CollectionInfo) -> Unit,
    onDayClick: (LocalDate) -> Unit,
) {
    Column(modifier = modifier) {
        CurrentCollections(
            onCollectionClick = onCollectionClick,
            viewModel = calendarViewModel
        )

        when (LocalConfiguration.current.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                PortraitCalendar(
                    state = calendarState,
                    viewModel = calendarViewModel,
                    onDayClick = onDayClick,
                )
            }

            else -> {
                LandscapeCalendar(
                    state = calendarState,
                    viewModel = calendarViewModel,
                    onDayClick = onDayClick,
                )
            }
        }

    }
}

@Composable
private fun CurrentCollections(
    onCollectionClick: (CollectionInfo) -> Unit,
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
    onCollectionClick: (CollectionInfo) -> Unit,
) {
    FilterChip(
        onClick = {
            current.enabled = !current.enabled
            onCollectionClick(current.name)
        },
        modifier = Modifier.padding(vertical = 0.dp),
        interactionSource = remember { MutableInteractionSource() },
        label = { Text(text = current.name.collectionName) },
        selected = current.enabled,
    )
}

fun String.capitalizeFirstLetter(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else "$it"
    }
}

@Composable
fun rememberCuroCalendarState(): CalendarState {
    return rememberCalendarState(
        startMonth = YearMonth.now().minusMonths(100),
        endMonth = YearMonth.now().plusMonths(100),
        firstVisibleMonth = YearMonth.now(),
        firstDayOfWeek = firstDayOfWeekFromLocale(),
        outDateStyle = OutDateStyle.EndOfRow,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxScope.Day(
    day: LocalDate,
    dayState: CalendarViewModel.DayState,
    onDayClick: (LocalDate) -> Unit
) {
    val (backGroundColorForToday, textColorForToday) =
        if (isCurrentDay(day)) {
            MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent to MaterialTheme.colorScheme.onBackground
        }
    BadgedBox(
        modifier = Modifier
            .clickable { onDayClick(day) }
            .align(Alignment.BottomStart)
            .padding(12.dp),
        badge = {
            when (dayState) {
                CalendarViewModel.DayState.Empty -> {}
                is CalendarViewModel.DayState.NoWarn -> {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ) {
                        Text(text = dayState.amount.toString())
                    }
                }

                is CalendarViewModel.DayState.Warn -> {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.error,
                    ) {
                        Text(text = dayState.amount.toString())
                    }
                }
            }
        },
    ) {
        Text(
            modifier = Modifier
                .background(backGroundColorForToday, CircleShape),
            text = "${day.dayOfMonth}",
            fontWeight = FontWeight.Light,
            color = textColorForToday,
        )
    }
}

@Composable
private fun isCurrentDay(day: LocalDate) =
    day == LocalDate.now()

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
    ) {
        for (dayOfWeek in daysOfWeek) {
            val textColorForCurrentDayOfWeek = if (
                isCurrentDayOfWeek(dayOfWeek)
            ) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onBackground
            }

            Text(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
                    .height(20.dp)
                    .background(cellsBackgroundColor),
                textAlign = TextAlign.Start,
                text = dayOfWeek.name.lowercase().capitalizeFirstLetter().take(3),
                fontWeight = FontWeight.Normal,
                color = textColorForCurrentDayOfWeek,
            )
        }
    }
}

private fun isCurrentDayOfWeek(
    dayOfWeek: DayOfWeek,
) = dayOfWeek == LocalDate.now().dayOfWeek

//@Preview(showBackground = true)
//@Composable
//fun CalendarPreview() {
//    val viewModel = remember { CalendarViewModel() }
//    CalendarScreen(viewModel, rememberCuroCalendarState(), onCollectionClick = {}, onDayClick = {})
//}
