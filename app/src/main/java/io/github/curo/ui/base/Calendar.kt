package io.github.curo.ui.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.ContentHeightMode
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.core.CalendarDay
import io.github.curo.ui.screens.capitalizeFirstLetter
import java.time.DayOfWeek
import java.time.LocalDateTime

private val pageBackgroundColor: Color @Composable get() = MaterialTheme.colorScheme.background

@Composable
fun Calendar(
    state: CalendarState,
    modifier: Modifier = Modifier,
) {
    Box {
        Box(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 0.2.dp)
                .fillMaxSize()
                .background(Color.LightGray)
        ) // draw dividers between cells

        HorizontalCalendar(
            modifier = modifier
                .wrapContentWidth()
                .background(Color.Transparent), // in case first box color will be visible
            state = state,
            dayContent = { Day(it) },
            contentHeightMode = ContentHeightMode.Fill,
            monthHeader = {month ->
                val daysOfWeek = remember {
                    month.weekDays.first().map { it.date.dayOfWeek }
                }
                DaysOfWeekTitle(daysOfWeek = daysOfWeek, calendarState = state)
            }
        )
    }
}

@Composable
fun Day(day: CalendarDay) {
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

        Box(
            modifier = Modifier
                .padding(4.dp)
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