package io.github.curo.utils

import java.time.format.DateTimeFormatter

object DateTimeUtils {
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    val timeShortFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
}