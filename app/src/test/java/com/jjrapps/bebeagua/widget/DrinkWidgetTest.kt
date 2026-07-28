package com.jjrapps.bebeagua.widget

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class DrinkWidgetTest {

    @Test
    fun `badge scales with the cell`() {
        // A 1x1 cell on an 8x6 grid is around 45dp: the badge must shrink with it.
        assertEquals(16.2f, badgeSize(45.dp).value, TOLERANCE)
        assertEquals(20.16f, badgeSize(56.dp).value, TOLERANCE)
    }

    @Test
    fun `badge stays legible on tiny cells`() {
        assertEquals(15f, badgeSize(32.dp).value, TOLERANCE)
        assertEquals(15f, badgeSize(20.dp).value, TOLERANCE)
    }

    @Test
    fun `badge stops growing on large cells`() {
        assertEquals(28f, badgeSize(200.dp).value, TOLERANCE)
    }

    @Test
    fun `badge never covers more than a third of the cell side`() {
        listOf(40, 56, 72, 96, 120).forEach { side ->
            val badge = badgeSize(side.dp)
            assert(badge.value <= side * 0.38f) { "badge $badge too big for ${side}dp" }
        }
    }
}

private const val TOLERANCE = 0.01f
