package io.github.curo.ui.base

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.core.daysOfWeek
import io.github.curo.viewmodels.CalendarViewModel
import io.github.curo.ui.screens.Day
import io.github.curo.ui.screens.DaysOfWeekTitle
import java.time.LocalDate

@Composable
fun LandscapeCalendar(
    modifier: Modifier = Modifier,
    state: CalendarState,
    viewModel: CalendarViewModel,
    onDayClick: (LocalDate) -> Unit,
) {
    VerticalCalendar(
        calendarScrollPaged = false,
        modifier = modifier
            .background(Color.Transparent),
        state = state,
        dayContent = { day ->
            Day(
                day.date,
                viewModel.dayState[day.date] ?: CalendarViewModel.DayState.Empty,
                onDayClick
            )
        },
        monthHeader = {
            val daysOfWeek = remember { daysOfWeek(state.firstDayOfWeek) }
            DaysOfWeekTitle(daysOfWeek = daysOfWeek)
        }
    )
}