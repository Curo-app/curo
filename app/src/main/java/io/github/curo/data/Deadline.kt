package io.github.curo.data

import androidx.compose.runtime.Immutable
import java.util.Date

@Immutable
sealed interface Deadline {
    val date: Date

    companion object {
        fun of(date: Date) = SimpleDeadline(date)
        fun of(date: Date, time: Date) = TimedDeadline(date, time)
    }
}

@Immutable
data class SimpleDeadline(
    override val date: Date,
) : Deadline

@Immutable
data class TimedDeadline(
    override val date: Date,
    val time: Date,
) : Deadline