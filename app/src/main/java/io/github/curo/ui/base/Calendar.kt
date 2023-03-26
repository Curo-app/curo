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
import io.github.curo.ui.screens.capitalizeFirstLetter
import java.time.DayOfWeek
import java.time.LocalDateTime

private val notes = generateNotes().groupBy { it.time.toLocalDate() }

private val pageBackgroundColor: Color @Composable get() = MaterialTheme.colorScheme.background

@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    state: CalendarState,
) {
    Box(modifier = modifier) {
        HorizontalCalendar(
            modifier = Modifier
                .wrapContentWidth()
                .background(Color.Transparent), // in case first box color will be visible
            state = state,
            dayContent = { day ->
                val notes = if (day.position == DayPosition.MonthDate) {
                    notes[day.date].orEmpty()
                } else {
                    emptyList()
                }
                Day(day, notes.sortedBy { it.time })
            },
            contentHeightMode = ContentHeightMode.Fill,
            monthHeader = { month ->
                val daysOfWeek = remember {
                    month.weekDays.first().map { it.date.dayOfWeek }
                }
                DaysOfWeekTitle(daysOfWeek = daysOfWeek, calendarState = state)
            }
        )
    }
}

@Composable
fun Day(day: CalendarDay, notes: List<CalendarNote>) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxHeight(1f)
            .fillMaxWidth(1f)
            .padding(0.2.dp)
            .background(pageBackgroundColor),
    ) {
        val backGroundColor = if (
            day.date == LocalDateTime.now().toLocalDate()
        ) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            Color.Transparent
        }

        val textColor = if (
            day.date == LocalDateTime.now().toLocalDate()
        ) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onBackground
        }

        Column(
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
                    .background(backGroundColor)
            ) {
                Text(
                    modifier = Modifier
                        .padding(2.dp),
                    text = "${day.date.dayOfMonth}",
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                    color = textColor,
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(1.dp),
            ) {
                notes.subList(0, notes.size.coerceAtMost(5)).forEach { note ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
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
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>, calendarState: CalendarState) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        for (dayOfWeek in daysOfWeek) {
            val textColor = if (
                dayOfWeek == LocalDateTime.now().dayOfWeek &&
                calendarState.firstVisibleMonth.yearMonth.month == LocalDateTime.now().month &&
                calendarState.firstVisibleMonth.yearMonth.year == LocalDateTime.now().year
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
                    .background(pageBackgroundColor),
                textAlign = TextAlign.Center,
                text = dayOfWeek.name.lowercase().capitalizeFirstLetter().take(3),
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = textColor,
            )
        }
    }
}