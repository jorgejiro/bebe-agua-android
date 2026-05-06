package com.jjrapps.bebeagua.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DayHistoryTest {

    private val date: LocalDate = LocalDate.of(2025, 6, 1)

    @Test
    fun `progressFraction is ratio of totalMl to goalMl`() {
        val history = DayHistory(date, totalMl = 1000, goalMl = 2000)
        assertEquals(0.5f, history.progressFraction)
    }

    @Test
    fun `progressFraction is zero when goal is zero`() {
        val history = DayHistory(date, totalMl = 0, goalMl = 0)
        assertEquals(0f, history.progressFraction)
    }

    @Test
    fun `isGoalReached is true when total equals goal`() {
        val history = DayHistory(date, totalMl = 1500, goalMl = 1500)
        assertTrue(history.isGoalReached)
    }

    @Test
    fun `isGoalReached is false when total is below goal`() {
        val history = DayHistory(date, totalMl = 1000, goalMl = 1500)
        assertFalse(history.isGoalReached)
    }
}
