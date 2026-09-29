package com.jjrapps.bebeagua.widget

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DrinkWideWidgetTest {

    @Test
    fun `percent is a whole number of the goal`() {
        assertEquals(52, progressPercent(1250, 2400))
        assertEquals(0, progressPercent(0, 2400))
    }

    @Test
    fun `percent is clamped and safe without a goal`() {
        assertEquals(100, progressPercent(3000, 2400))
        assertEquals(0, progressPercent(500, 0))
    }

    @Test
    fun `goal reached only at or above a valid goal`() {
        assertTrue(isGoalReached(2400, 2400))
        assertTrue(isGoalReached(2500, 2400))
        assertFalse(isGoalReached(2399, 2400))
        assertFalse(isGoalReached(100, 0))
    }

    @Test
    fun `icon fits inside the cell height`() {
        listOf(45, 57, 80).forEach { h ->
            val layout = wideLayout(180.dp, h.dp)
            assertTrue(layout.iconSide + layout.padding * 2 <= h.dp)
        }
    }

    @Test
    fun `icon never takes more than its share of a narrow cell`() {
        val layout = wideLayout(110.dp, 57.dp)
        assertTrue(layout.iconSide.value <= 110 * 0.38f + 0.01f)
    }

    @Test
    fun `text scales with height on a roomy cell`() {
        val small = wideLayout(300.dp, 45.dp).primaryTextSize.value
        val big = wideLayout(300.dp, 70.dp).primaryTextSize.value
        assertTrue(big > small)
    }

    @Test
    fun `text shrinks when the width left is small`() {
        val roomy = wideLayout(300.dp, 57.dp).primaryTextSize.value
        val narrow = wideLayout(110.dp, 57.dp).primaryTextSize.value
        assertTrue(narrow < roomy)
    }

    @Test
    fun `text sizes stay within clamps`() {
        val tiny = wideLayout(20.dp, 20.dp)
        assertEquals(10f, tiny.primaryTextSize.value, 0.01f)
        assertEquals(9f, tiny.secondaryTextSize.value, 0.01f)
        val huge = wideLayout(900.dp, 400.dp)
        assertEquals(22f, huge.primaryTextSize.value, 0.01f)
        assertEquals(15f, huge.secondaryTextSize.value, 0.01f)
    }
}
