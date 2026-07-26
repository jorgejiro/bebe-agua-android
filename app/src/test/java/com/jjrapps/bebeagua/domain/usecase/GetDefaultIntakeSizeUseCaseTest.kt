package com.jjrapps.bebeagua.domain.usecase

import com.jjrapps.bebeagua.domain.model.AppSettings
import com.jjrapps.bebeagua.domain.repository.IntakeRepository
import com.jjrapps.bebeagua.domain.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetDefaultIntakeSizeUseCaseTest {

    private val intakeRepository = mockk<IntakeRepository>()
    private val settingsRepository = mockk<SettingsRepository>()
    private val useCase = GetDefaultIntakeSizeUseCase(intakeRepository, settingsRepository)

    @Test
    fun `returns the last logged amount`() = runTest {
        coEvery { intakeRepository.getLastIntakeSizeMl() } returns 330
        every { settingsRepository.observeSettings() } returns flowOf(settings(sizes = listOf(200, 500)))

        assertEquals(330, useCase())
    }

    @Test
    fun `returns the first configured size when nothing was logged yet`() = runTest {
        coEvery { intakeRepository.getLastIntakeSizeMl() } returns null
        every { settingsRepository.observeSettings() } returns flowOf(settings(sizes = listOf(250, 500)))

        assertEquals(250, useCase())
    }

    private fun settings(sizes: List<Int>) = AppSettings(
        dailyGoalMl = 2400,
        dayStartMinutes = 480,
        dayEndMinutes = 1260,
        remindersPerDay = 14,
        intakeSizesMl = sizes,
        language = "auto"
    )
}
