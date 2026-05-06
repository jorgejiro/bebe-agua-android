package com.jjrapps.bebeagua.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DaySummaryTest {

    private val date: LocalDate = LocalDate.of(2025, 6, 1)

    @Test
    fun `progressFraction is ratio of consumed to goal`() {
        val summary = DaySummary(date, consumedMl = 750, goalMl = 1500, intakes = emptyList())
        assertEquals(0.5f, summary.progressFraction)
    }

    @Test
    fun `progressFraction exceeds 1 when consumed is above goal`() {
        val summary = DaySummary(date, consumedMl = 2000, goalMl = 1500, intakes = emptyList())
        assertEquals(2000f / 1500f, summary.progressFraction)
    }

    @Test
    fun `progressFraction is zero when goal is zero`() {
        val summary = DaySummary(date, consumedMl = 500, goalMl = 0, intakes = emptyList())
        assertEquals(0f, summary.progressFraction)
    }

    @Test
    fun `isGoalReached is true when consumed equals goal`() {
        val summary = DaySummary(date, consumedMl = 1500, goalMl = 1500, intakes = emptyList())
        assertTrue(summary.isGoalReached)
    }

    @Test
    fun `isGoalReached is true when consumed exceeds goal`() {
        val summary = DaySummary(date, consumedMl = 1800, goalMl = 1500, intakes = emptyList())
        assertTrue(summary.isGoalReached)
    }

    @Test
    fun `isGoalReached is false when consumed is below goal`() {
        val summary = DaySummary(date, consumedMl = 1400, goalMl = 1500, intakes = emptyList())
        assertFalse(summary.isGoalReached)
    }
}
