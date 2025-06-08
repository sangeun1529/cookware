package io.github.jisungbin.erratum.time

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.Month
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TimeProviderTest {

    @AfterEach
    fun tearDown() {
        // 각 테스트 후 고정된 시간을 초기화하여 다른 테스트에 영향을 주지 않도록 합니다.
        TimeProvider.clearFixedNow()
    }

    @Test
    fun `testNow_returnsCurrentTime`() {
        val before = LocalDateTime.now().minusSeconds(1) // 1초 전
        val nowFromProvider = TimeProvider.now()
        val after = LocalDateTime.now().plusSeconds(1) // 1초 후

        assertTrue(nowFromProvider.isAfter(before) && nowFromProvider.isBefore(after),
                   "now() should return current time within a small delta")
    }

    @Test
    fun `testNow_returnsFixedTime_whenFixed`() {
        val fixedDateTime = LocalDateTime.of(2023, Month.JANUARY, 1, 10, 30, 0)
        TimeProvider.fixedNow(fixedDateTime)

        val nowFromProvider = TimeProvider.now()

        assertEquals(fixedDateTime, nowFromProvider, "now() should return the fixed time")
    }

    @Test
    fun `testClearFixedNow_restoresCurrentTime`() {
        val fixedDateTime = LocalDateTime.of(2023, Month.JANUARY, 1, 10, 30, 0)
        TimeProvider.fixedNow(fixedDateTime)

        assertEquals(fixedDateTime, TimeProvider.now(), "Time should be fixed initially")

        TimeProvider.clearFixedNow()

        val before = LocalDateTime.now().minusSeconds(1)
        val nowFromProvider = TimeProvider.now()
        val after = LocalDateTime.now().plusSeconds(1)

        assertTrue(nowFromProvider.isAfter(before) && nowFromProvider.isBefore(after),
                   "now() should return current time after clearing fixed time")
    }

    @Test
    fun `testFormatToString`() {
        val dateTime = LocalDateTime.of(2023, Month.OCTOBER, 26, 14, 35, 50)
        val pattern = "yyyy-MM-dd HH:mm:ss"
        val expectedString = "2023-10-26 14:35:50"

        val formattedString = TimeProvider.formatToString(dateTime, pattern)

        assertEquals(expectedString, formattedString, "formatToString should format LocalDateTime correctly")
    }

    @Test
    fun `testParseToDateTime`() {
        val dateTimeString = "2023-10-26 14:35:50"
        val pattern = "yyyy-MM-dd HH:mm:ss"
        val expectedDateTime = LocalDateTime.of(2023, Month.OCTOBER, 26, 14, 35, 50)

        val parsedDateTime = TimeProvider.parseToDateTime(dateTimeString, pattern)

        assertEquals(expectedDateTime, parsedDateTime, "parseToDateTime should parse string to LocalDateTime correctly")
    }

    @Test
    fun `testDaysBetween`() {
        val startDateTime = LocalDateTime.of(2023, Month.OCTOBER, 20, 10, 0, 0)
        val endDateTime = LocalDateTime.of(2023, Month.OCTOBER, 23, 12, 0, 0)
        val expectedDays = 3L

        val actualDays = TimeProvider.daysBetween(startDateTime, endDateTime)

        assertEquals(expectedDays, actualDays, "daysBetween should calculate the difference in days correctly")
    }

    @Test
    fun `testHoursBetween`() {
        val startDateTime = LocalDateTime.of(2023, Month.OCTOBER, 26, 10, 0, 0)
        val endDateTime = LocalDateTime.of(2023, Month.OCTOBER, 26, 13, 30, 0)
        val expectedHours = 3L

        val actualHours = TimeProvider.hoursBetween(startDateTime, endDateTime)

        assertEquals(expectedHours, actualHours, "hoursBetween should calculate the difference in hours correctly")
    }

    @Test
    fun `testMinutesBetween`() {
        val startDateTime = LocalDateTime.of(2023, Month.OCTOBER, 26, 10, 0, 0)
        val endDateTime = LocalDateTime.of(2023, Month.OCTOBER, 26, 10, 45, 30)
        val expectedMinutes = 45L

        val actualMinutes = TimeProvider.minutesBetween(startDateTime, endDateTime)

        assertEquals(expectedMinutes, actualMinutes, "minutesBetween should calculate the difference in minutes correctly")
    }
}
