package io.foreshore.cookware.time

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object TimeProvider {

    private val fixedTime = ThreadLocal<LocalDateTime>()

    fun now(): LocalDateTime {
        return fixedTime.get() ?: LocalDateTime.now()
    }

    fun fixedNow(fixedDateTime: LocalDateTime) {
        fixedTime.set(fixedDateTime)
    }

    fun clearFixedNow() {
        fixedTime.remove()
    }

    fun formatToString(dateTime: LocalDateTime, pattern: String): String {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern))
    }

    fun parseToDateTime(dateTimeString: String, pattern: String): LocalDateTime {
        return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern(pattern))
    }

    fun daysBetween(startDateTime: LocalDateTime, endDateTime: LocalDateTime): Long {
        return ChronoUnit.DAYS.between(startDateTime, endDateTime)
    }

    fun hoursBetween(startDateTime: LocalDateTime, endDateTime: LocalDateTime): Long {
        return ChronoUnit.HOURS.between(startDateTime, endDateTime)
    }

    fun minutesBetween(startDateTime: LocalDateTime, endDateTime: LocalDateTime): Long {
        return ChronoUnit.MINUTES.between(startDateTime, endDateTime)
    }
}
