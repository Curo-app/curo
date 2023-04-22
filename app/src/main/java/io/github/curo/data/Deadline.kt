package io.github.curo.data

import androidx.compose.runtime.Immutable
import java.time.LocalDate
import java.time.LocalTime

@Immutable
sealed interface Deadline {
    val date: LocalDate

    companion object {
        fun of(date: LocalDate) = SimpleDeadline(date)
        fun of(date: LocalDate, time: LocalTime) = TimedDeadline(date, time)
        fun of(dateOpt: LocalDate?, timeOpt: LocalTime?) =
            dateOpt?.let { date ->
                timeOpt?.let { time ->
                    TimedDeadline(date, time)
                } ?: SimpleDeadline(date)
            }
    }
}

@Immutable
data class SimpleDeadline(
    override val date: LocalDate,
) : Deadline

@Immutable
data class TimedDeadline(
    override val date: LocalDate,
    val time: LocalTime,
) : Deadline