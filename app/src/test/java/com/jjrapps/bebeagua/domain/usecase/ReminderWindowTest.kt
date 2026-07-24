package com.jjrapps.bebeagua.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalTime

class ReminderWindowTest {

    @Test
    fun `reminderCutoff returns the same time when there is no offset`() {
        val now = LocalTime.of(10, 0)
        assertEquals(now, reminderCutoff(now, 0L))
    }

    @Test
    fun `reminderCutoff adds the offset`() {
        assertEquals(LocalTime.of(10, 15), reminderCutoff(LocalTime.of(10, 0), 15L))
    }

    @Test
    fun `reminderCutoff returns null when the offset crosses midnight`() {
        assertNull(reminderCutoff(LocalTime.of(23, 50), 15L))
    }

    @Test
    fun `graceWindowCutoff returns now when there is no intake today`() {
        val now = LocalTime.of(10, 0)
        assertEquals(now, graceWindowCutoff(now, lastIntakeAt = null, graceMinutes = 15))
    }

    @Test
    fun `graceWindowCutoff pushes the cutoff past the grace window`() {
        val cutoff = graceWindowCutoff(
            now = LocalTime.of(10, 58),
            lastIntakeAt = LocalTime.of(10, 58),
            graceMinutes = 15
        )
        assertEquals(LocalTime.of(11, 13), cutoff)
    }

    @Test
    fun `graceWindowCutoff returns now when the grace window is already over`() {
        val now = LocalTime.of(11, 30)
        val cutoff = graceWindowCutoff(now, lastIntakeAt = LocalTime.of(10, 0), graceMinutes = 15)
        assertEquals(now, cutoff)
    }

    @Test
    fun `graceWindowCutoff returns now when the feature is disabled by a zero window`() {
        val now = LocalTime.of(10, 0)
        assertEquals(now, graceWindowCutoff(now, lastIntakeAt = LocalTime.of(9, 59), graceMinutes = 0))
    }

    @Test
    fun `graceWindowCutoff returns null when the grace window crosses midnight`() {
        assertNull(
            graceWindowCutoff(
                now = LocalTime.of(23, 55),
                lastIntakeAt = LocalTime.of(23, 50),
                graceMinutes = 15
            )
        )
    }
}
