package com.jjrapps.bebeagua.domain.usecase

import app.cash.turbine.test
import com.jjrapps.bebeagua.domain.model.AppSettings
import com.jjrapps.bebeagua.domain.model.Intake
import com.jjrapps.bebeagua.domain.repository.IntakeRepository
import com.jjrapps.bebeagua.domain.repository.SettingsRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class GetTodaySummaryUseCaseTest {

    private val intakeRepository = mockk<IntakeRepository>()
    private val settingsRepository = mockk<SettingsRepository>()
    private val useCase = GetTodaySummaryUseCase(intakeRepository, settingsRepository)

    @Test
    fun `sums all intake amounts from repository`() = runTest {
        val today = LocalDate.now()
        every { intakeRepository.observeIntakesForDate(today) } returns flowOf(
            listOf(intake(id = 1L, amountMl = 250, date = today), intake(id = 2L, amountMl = 300, date = today))
        )
        every { settingsRepository.observeSettings() } returns flowOf(defaultSettings())

        useCase().test {
            val summary = awaitItem()
            assertEquals(550, summary.consumedMl)
            assertEquals(2, summary.intakes.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `returns zero consumed when no intakes recorded`() = runTest {
        val today = LocalDate.now()
        every { intakeRepository.observeIntakesForDate(today) } returns flowOf(emptyList())
        every { settingsRepository.observeSettings() } returns flowOf(defaultSettings())

        useCase().test {
            assertEquals(0, awaitItem().consumedMl)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `goal in summary comes from settings`() = runTest {
        val today = LocalDate.now()
        every { intakeRepository.observeIntakesForDate(today) } returns flowOf(emptyList())
        every { settingsRepository.observeSettings() } returns flowOf(defaultSettings(goal = 2000))

        useCase().test {
            assertEquals(2000, awaitItem().goalMl)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun intake(id: Long, amountMl: Int, date: LocalDate) = Intake(
        id = id,
        amountMl = amountMl,
        timestampEpochMs = System.currentTimeMillis(),
        timezoneId = "Europe/Madrid",
        localDate = date
    )

    private fun defaultSettings(goal: Int = 1500) = AppSettings(
        dailyGoalMl = goal,
        dayStartMinutes = 480,
        dayEndMinutes = 1380,
        remindersPerDay = 6,
        intakeSizesMl = listOf(200),
        language = "auto"
    )
}
