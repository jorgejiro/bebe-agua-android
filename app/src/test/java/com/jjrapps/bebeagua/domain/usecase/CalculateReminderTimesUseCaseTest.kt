package com.jjrapps.bebeagua.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime

class CalculateReminderTimesUseCaseTest {

    private val useCase = CalculateReminderTimesUseCase()

    @Test
    fun `returns empty list when count is zero`() {
        assertTrue(useCase(480, 1380, 0).isEmpty())
    }

    @Test
    fun `returns empty list when start equals end`() {
        assertTrue(useCase(480, 480, 3).isEmpty())
    }

    @Test
    fun `returns empty list when start is after end`() {
        assertTrue(useCase(1380, 480, 3).isEmpty())
    }

    @Test
    fun `first reminder anchored at window start, rest evenly spaced`() {
        // window 480..1380 (900 min), 6 reminders → interval = 900/5 = 180 min
        // → 480 (08:00), 660 (11:00), 840 (14:00), 1020 (17:00), 1200 (20:00), 1380 (23:00)
        val result = useCase(480, 1380, 6)
        assertEquals(6, result.size)
        assertEquals(LocalTime.of(8, 0), result[0])
        assertEquals(LocalTime.of(11, 0), result[1])
        assertEquals(LocalTime.of(14, 0), result[2])
        assertEquals(LocalTime.of(17, 0), result[3])
        assertEquals(LocalTime.of(20, 0), result[4])
        assertEquals(LocalTime.of(23, 0), result[5])
    }

    @Test
    fun `single reminder is placed at start of window`() {
        val result = useCase(480, 1380, 1)
        assertEquals(1, result.size)
        assertEquals(LocalTime.of(8, 0), result[0])
    }

    @Test
    fun `returns exactly the requested count of reminders`() {
        assertEquals(3, useCase(480, 1380, 3).size)
        assertEquals(8, useCase(480, 1380, 8).size)
    }

    @Test
    fun `all reminder times fall within the configured window`() {
        val windowStart = LocalTime.of(8, 0)
        val windowEnd = LocalTime.of(23, 0)
        useCase(480, 1380, 8).forEach { time ->
            assertTrue("$time should be >= windowStart", !time.isBefore(windowStart))
            assertTrue("$time should be <= windowEnd", !time.isAfter(windowEnd))
        }
    }

    @Test
    fun `reminder times are in ascending order`() {
        val result = useCase(480, 1380, 5)
        for (i in 0 until result.size - 1) {
            assertTrue("${result[i]} should be before ${result[i + 1]}", result[i].isBefore(result[i + 1]))
        }
    }
}
