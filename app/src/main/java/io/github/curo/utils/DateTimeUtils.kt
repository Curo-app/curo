package io.github.curo.utils

import android.icu.text.DateFormat

object DateTimeUtils {
    val dateFormatter: DateFormat = DateFormat.getDateInstance()
    val timeShortFormatter: DateFormat = DateFormat.getTimeInstance(DateFormat.SHORT)
}