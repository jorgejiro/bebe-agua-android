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
import java.time.LocalDate

class GetDailyHistoryUseCaseTest {

    private val intakeRepository = mockk<IntakeRepository>()
    private val settingsRepository = mockk<SettingsRepository>()
    private val useCase = GetDailyHistoryUseCase(intakeRepository, settingsRepository)

    @Test
    fun `generates an entry for every date in the range ordered newest first`() = runTest {
        val from = LocalDate.of(2025, 1, 1)
        val to = LocalDate.of(2025, 1, 3)
        every { settingsRepository.observeSettings() } returns flowOf(defaultSettings())
        coEvery { intakeRepository.getDailyTotals(from, to) } returns emptyMap()

        val result = useCase(from, to)

        assertEquals(3, result.size)
        assertEquals(LocalDate.of(2025, 1, 3), result[0].date)
        assertEquals(LocalDate.of(2025, 1, 2), result[1].date)
        assertEquals(LocalDate.of(2025, 1, 1), result[2].date)
    }

    @Test
    fun `maps repository totals and fills missing days with zero`() = runTest {
        val from = LocalDate.of(2025, 1, 1)
        val to = LocalDate.of(2025, 1, 3)
        every { settingsRepository.observeSettings() } returns flowOf(defaultSettings())
        coEvery { intakeRepository.getDailyTotals(from, to) } returns mapOf(
            LocalDate.of(2025, 1, 2) to 800
        )

        val result = useCase(from, to)

        assertEquals(0, result.first { it.date == LocalDate.of(2025, 1, 3) }.totalMl)
        assertEquals(800, result.first { it.date == LocalDate.of(2025, 1, 2) }.totalMl)
        assertEquals(0, result.first { it.date == LocalDate.of(2025, 1, 1) }.totalMl)
    }

    @Test
    fun `all entries carry the goal from settings`() = runTest {
        val from = LocalDate.of(2025, 3, 1)
        val to = LocalDate.of(2025, 3, 2)
        every { settingsRepository.observeSettings() } returns flowOf(defaultSettings(goal = 2000))
        coEvery { intakeRepository.getDailyTotals(from, to) } returns emptyMap()

        val result = useCase(from, to)

        result.forEach { assertEquals(2000, it.goalMl) }
    }

    private fun defaultSettings(goal: Int = 1500) = AppSettings(
        dailyGoalMl = goal,
        dayStartMinutes = 480,
        dayEndMinutes = 1380,
        remindersPerDay = 6,
        intakeSizesMl = listOf(200),
        language = "auto"
    )
}
