package io.github.curo.ui.base

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime
import java.time.YearMonth

@Immutable
data class CalendarNote(
    val time: LocalDateTime,
    val name: String,
    val description: String? = null,
    val color: Color = Color.Unspecified,
)

fun generateNotes(): List<CalendarNote> = buildList {
    val currentMonth = YearMonth.now()

    currentMonth.atDay(17).also { date ->
        add(
            CalendarNote(
                time = date.atTime(14, 0),
                name = "Buy milk",
                color = Color.Red,
            ),
        )
        add(
            CalendarNote(
                time = date.atTime(21, 30),
                name = "FP homework",
                color = Color.Blue,
            ),
        )
    }

    currentMonth.atDay(25).also { date ->
        add(
            CalendarNote(
                time = date.atTime(13, 20),
                name = "Interview at 14:00 PM at Google office",
                color = Color.Green,
            ),
        )
        add(
            CalendarNote(
                time = date.atTime(16, 0),
                name = "Buy a new phone for my mom on AliExpress for her birthday",
                color = Color.Cyan,
            ),
        )
        add(
            CalendarNote(
                time = date.atTime(9, 40),
                name = "Take a walk with my dog at 18:00 PM at the park",
                color = Color.Yellow,
            ),
        )
        add(
            CalendarNote(
                time = date.atTime(10, 40),
                name = "Finish the Curo app by 23:59 PM today",
                color = Color.LightGray,
            ),
        )
        add(
            CalendarNote(
                time = date.atTime(8, 40),
                name = "Learn how to use the new Android Studio 2021.3.1 Canary 1",
                color = Color.Magenta,
            ),
        )
        add(
            CalendarNote(
                time = date.atTime(12, 40),
                name = "Learn how to use the new Android Studio 2021.3.1 Canary 1",
                color = Color.Blue,
            ),
        )
    }
}