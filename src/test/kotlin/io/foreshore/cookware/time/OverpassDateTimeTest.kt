package io.foreshore.cookware.time

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.format.DateTimeParseException
import kotlin.test.assertTrue

/**
 *
 * @author sangeun
 * @date 2022/04/13
 */
class OverpassDateTimeTest {
    @Test
    fun of() {
        val overpass = OverpassDateTime.of(LocalDate.now(), "204:55")

        println(overpass)
        println(overpass.toLocalDateTime())
        println(overpass.toOverpassFormat())
        assertTrue { overpass.isOverpass() }
    }

    @Test
    fun ofDateTimeParseException() {
        assertThrows<DateTimeParseException> {
            OverpassDateTime.of(LocalDate.now(), "26")
        }
        assertThrows<DateTimeParseException> {
            OverpassDateTime.of(LocalDate.now(), "26:60")
        }
        assertThrows<DateTimeParseException> {
            OverpassDateTime.of(LocalDate.now(), "26:30:60")
        }
    }

    @Test
    fun ofIllegalOverpassDateTimeFormatException() {
        assertThrows<IllegalOverpassDateTimeFormatException> {
            OverpassDateTime.of(LocalDate.now(), "한글")
        }
    }
}
