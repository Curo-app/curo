package io.github.curo.ui.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.ContentHeightMode
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import io.github.curo.data.Deadline
import io.github.curo.data.Note
import io.github.curo.ui.screens.capitalizeFirstLetter
import java.time.DayOfWeek
import java.time.LocalDate

private val cellsBackgroundColor: Color @Composable get() = MaterialTheme.colorScheme.background

@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    state: CalendarState,
    notesState: List<Note>
) {
    HorizontalCalendar(
        modifier = modifier
            .wrapContentWidth()
            .background(Color.Transparent),
        state = state,
        dayContent = { day ->
            val todayNotes = getNotesForToday(notesState, day)
            Day(day, todayNotes)
        },
        contentHeightMode = ContentHeightMode.Fill,
        monthHeader = { month ->
            val daysOfWeek = remember {
                month.weekDays.first().map { it.date.dayOfWeek }
            }
            DaysOfWeekTitle(daysOfWeek = daysOfWeek, calendarState = state)
        },
    )
}

@Composable
private fun getNotesForToday(
    notesState: List<Note>,
    day: CalendarDay
): List<Note> {
    val notesWithDeadline =
        notesState
            .filterNot { it.deadline == null }
            .groupBy { it.deadline }

    val todayNotes =
        if (day.position == DayPosition.MonthDate) {
            notesWithDeadline[Deadline.of(day.date)].orEmpty()
        } else {
            emptyList()
        }

    return todayNotes
}

@Composable
fun Day(day: CalendarDay, notes: List<Note>) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxHeight(1f)
            .fillMaxWidth(1f)
            .padding(0.2.dp)
            .background(cellsBackgroundColor),
    ) {
        val (backGroundColorForToday, textColorForToday) =
            if (isCurrentDay(day)) {
                MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
            } else {
                Color.Transparent to MaterialTheme.colorScheme.onBackground
            }

        Column(
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            // TODO: fix background clipping for large dates
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(backGroundColorForToday, CircleShape)
            ) {
                Text(
                    modifier = Modifier
                        .padding(2.dp),
                    text = "${day.date.dayOfMonth}",
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                    color = textColorForToday,
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(1.dp),
            ) {
                notes.subList(0, notes.size.coerceAtMost(5)).forEach { note ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(note.color)
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(2.dp),
                            text = note.name,
                            maxLines = 1,
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Medium,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                // TODO: draw ellipsis if there are more than 5 notes
            }
        }

    }
}

@Composable
private fun isCurrentDay(day: CalendarDay) =
    day.date == LocalDate.now()

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>, calendarState: CalendarState) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        for (dayOfWeek in daysOfWeek) {
            val textColorForCurrentDayOfWeek = if (
                isCurrentDayOfWeek(dayOfWeek, calendarState)
            ) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onBackground
            }

            Text(
                modifier = Modifier
                    .padding(0.2.dp)
                    .weight(1f)
                    .height(20.dp)
                    .background(cellsBackgroundColor),
                textAlign = TextAlign.Center,
                text = dayOfWeek.name.lowercase().capitalizeFirstLetter().take(3),
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = textColorForCurrentDayOfWeek,
            )
        }
    }
}

@Composable
private fun isCurrentDayOfWeek(
    dayOfWeek: DayOfWeek,
    calendarState: CalendarState
) = dayOfWeek == LocalDate.now().dayOfWeek &&
        calendarState.firstVisibleMonth.yearMonth.month == LocalDate.now().month &&
        calendarState.firstVisibleMonth.yearMonth.year == LocalDate.now().year