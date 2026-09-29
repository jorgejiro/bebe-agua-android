package com.jjrapps.bebeagua.widget

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class WidgetMidnightTest {

    private val madrid = ZoneId.of("Europe/Madrid")

    private fun clockAt(iso: String) = Clock.fixed(Instant.parse(iso), madrid)

    private fun expected(year: Int, month: Int, day: Int): Long =
        ZonedDateTime.of(year, month, day, 0, 0, 0, 0, madrid).toInstant().toEpochMilli()

    @Test
    fun `midday resolves to the next local midnight`() {
        // 2026-07-26 12:00 Madrid (CEST, UTC+2)
        assertEquals(expected(2026, 7, 27), nextMidnightMillis(clockAt("2026-07-26T10:00:00Z")))
    }

    @Test
    fun `just after midnight resolves to the following midnight`() {
        // 2026-07-26 00:00:30 Madrid
        assertEquals(expected(2026, 7, 27), nextMidnightMillis(clockAt("2026-07-25T22:00:30Z")))
    }

    @Test
    fun `just before midnight resolves to that midnight`() {
        // 2026-07-26 23:59:59 Madrid
        assertEquals(expected(2026, 7, 27), nextMidnightMillis(clockAt("2026-07-26T21:59:59Z")))
    }

    @Test
    fun `spring forward day is 23 hours long`() {
        // 2026-03-29 10:00 Madrid: the day started at 23:00Z and ends at 22:00Z next day.
        val next = nextMidnightMillis(clockAt("2026-03-29T08:00:00Z"))
        assertEquals(Instant.parse("2026-03-29T22:00:00Z").toEpochMilli(), next)
        assertEquals(expected(2026, 3, 30), next)
    }

    @Test
    fun `fall back day is 25 hours long`() {
        // 2026-10-25 10:00 Madrid: the day ends at 2026-10-25T23:00Z (midnight is UTC+1 again).
        val next = nextMidnightMillis(clockAt("2026-10-25T09:00:00Z"))
        assertEquals(Instant.parse("2026-10-25T23:00:00Z").toEpochMilli(), next)
        assertEquals(expected(2026, 10, 26), next)
    }
}
