package io.github.curo.database.converter

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

class Converters {
    @TypeConverter
    fun fromTimestampToLocalDate(ts: Long?): LocalDate? =
        ts?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun fromLocalDateToTimestamp(date: LocalDate?): Long? =
        date?.toEpochDay()

    @TypeConverter
    fun fromTimestampToLocalTime(ts: Long?): LocalTime? =
        ts?.let { LocalTime.ofNanoOfDay(it) }

    @TypeConverter
    fun fromLocalTimeToTimestamp(time: LocalTime?): Long? =
        time?.toNanoOfDay()
}