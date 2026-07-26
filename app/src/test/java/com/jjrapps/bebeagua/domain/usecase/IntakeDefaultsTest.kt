package com.jjrapps.bebeagua.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class IntakeDefaultsTest {

    @Test
    fun `last used amount wins over configured sizes`() {
        assertEquals(330, resolveDefaultIntakeSize(lastUsedMl = 330, configuredSizesMl = listOf(200, 500)))
    }

    @Test
    fun `falls back to first configured size when nothing was logged yet`() {
        assertEquals(500, resolveDefaultIntakeSize(lastUsedMl = null, configuredSizesMl = listOf(500, 200)))
    }

    @Test
    fun `falls back to the constant when there are no configured sizes`() {
        assertEquals(
            FALLBACK_INTAKE_SIZE_ML,
            resolveDefaultIntakeSize(lastUsedMl = null, configuredSizesMl = emptyList())
        )
    }
}
