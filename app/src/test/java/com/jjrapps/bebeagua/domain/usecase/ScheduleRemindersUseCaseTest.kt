package com.jjrapps.bebeagua.domain.usecase

import com.jjrapps.bebeagua.domain.model.AppSettings
import com.jjrapps.bebeagua.domain.repository.IntakeRepository
import com.jjrapps.bebeagua.domain.repository.ReminderScheduler
import com.jjrapps.bebeagua.domain.repository.SettingsRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.LocalDate

class ScheduleRemindersUseCaseTest {

    private val settingsRepository = mockk<SettingsRepository>()
    private val intakeRepository = mockk<IntakeRepository>()
    private val calculateReminderTimes = CalculateReminderTimesUseCase()
    private val reminderScheduler = mockk<ReminderScheduler>(relaxed = true)
    private val useCase = ScheduleRemindersUseCase(
        settingsRepository, intakeRepository, calculateReminderTimes, reminderScheduler
    )

    @Test
    fun `schedules tomorrow when daily goal is already reached`() = runTest {
        val today = LocalDate.now()
        every { settingsRepository.observeSettings() } returns flowOf(defaultSettings(goal = 1500))
        every { intakeRepository.observeTotalForDate(today) } returns flowOf(1500)

        useCase()

        verify(exactly = 0) { reminderScheduler.cancel() }
        verify(exactly = 1) { reminderScheduler.scheduleNext(any(), any()) }
    }

    @Test
    fun `schedules tomorrow when consumed exceeds goal`() = runTest {
        val today = LocalDate.now()
        every { settingsRepository.observeSettings() } returns flowOf(defaultSettings(goal = 1500))
        every { intakeRepository.observeTotalForDate(today) } returns flowOf(2000)

        useCase()

        verify(exactly = 0) { reminderScheduler.cancel() }
        verify(exactly = 1) { reminderScheduler.scheduleNext(any(), any()) }
    }

    @Test
    fun `cancels when reminders count is zero`() = runTest {
        val today = LocalDate.now()
        // reminders = 0 → CalculateReminderTimesUseCase returns empty list → cancel
        every { settingsRepository.observeSettings() } returns flowOf(defaultSettings(reminders = 0))
        every { intakeRepository.observeTotalForDate(today) } returns flowOf(0)

        useCase()

        verify(exactly = 1) { reminderScheduler.cancel() }
        verify(exactly = 0) { reminderScheduler.scheduleNext(any(), any()) }
    }

    private fun defaultSettings(goal: Int = 1500, reminders: Int = 6) = AppSettings(
        dailyGoalMl = goal,
        dayStartMinutes = 0,
        dayEndMinutes = 1439,
        remindersPerDay = reminders,
        intakeSizesMl = listOf(200, 300, 500),
        language = "auto"
    )
}
