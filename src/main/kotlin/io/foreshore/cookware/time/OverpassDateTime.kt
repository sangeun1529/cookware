package io.foreshore.cookware.time

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * DateTime class 는 24시가 넘어간 숫자는 표현하지 못한다.
 * 영화 스케쥴 등록에는 27:02 , 30:11 등의 표현을 해야할 경우가 존재한다.
 * localDateTime 을 근간으로 한다. ( zoned 는 고려하지 않는다. )
 *
 * @author kkomac
 * @date 2023/01/11
 */

data class OverpassDateTime(
    val dateTime: LocalDateTime,
    val daysTo: Long
) {
    companion object {
        const val DAY_HOUR = 24

        /**
         * e.g.> @param date = "2022-04-11" ,@param time = "25:30" 일 경우 @return 2022-04-12 01:30
         * e.g.> @param date = "2022-04-11" ,@param time = "49:30" 일 경우 @return 2022-04-13 01:30
         */
        fun of(date: LocalDate, time: String): OverpassDateTime {
            val timeSeparator = ":"
            val startTimeSplit = time.split(timeSeparator).toMutableList()
            val hour: Long

            try {
                hour = startTimeSplit[0].toLong()
            } catch (e: NumberFormatException) {
                throw IllegalOverpassDateTimeFormatException(e.message!!, time, 0)
            }

            return if (hour >= DAY_HOUR) {
                val daysToAdd = hour / DAY_HOUR // quotient
                val realHour = hour % DAY_HOUR // remainder
                var temp = realHour.toString()
                if (temp.length < 2) temp = "0$temp"
                startTimeSplit[0] = temp
                create(
                    LocalDateTime.of(
                        date.plusDays(daysToAdd),
                        LocalTime.parse(startTimeSplit.joinToString(timeSeparator))
                    ),
                    daysToAdd
                )
            } else {
                create(LocalDateTime.of(date, LocalTime.parse(time)), 0)
            }
        }

        fun create(dateTime: LocalDateTime, daysToAdd: Long = 0): OverpassDateTime {
            return OverpassDateTime(dateTime, daysToAdd)
        }
    }

    fun isOverpass() = daysTo > 0
    fun toLocalDateTime() = dateTime
    fun toOverpassFormat(): String {
        val date = dateTime.toLocalDate().minusDays(daysTo).format(DateTimeFormatter.ISO_LOCAL_DATE)
        var time = dateTime.toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME)
        if (isOverpass()) {
            val timeSeparator = ":"
            val startTimeSplit = time.split(timeSeparator).toMutableList()
            startTimeSplit[0] = (startTimeSplit[0].toLong() + (daysTo * DAY_HOUR)).toString()
            time = startTimeSplit.joinToString(timeSeparator)
        }
        return "$date $time"
    }

    @Override
    override fun toString(): String {
        return toOverpassFormat()
    }
}

class IllegalOverpassDateTimeFormatException(message: String, text: String, index: Int) :
    DateTimeParseException(message, text, index)
