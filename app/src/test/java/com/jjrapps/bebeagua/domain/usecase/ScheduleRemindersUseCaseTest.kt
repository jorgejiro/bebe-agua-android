package com.jjrapps.bebeagua.domain.usecase

import com.jjrapps.bebeagua.domain.model.AppSettings
import com.jjrapps.bebeagua.domain.model.Intake
import com.jjrapps.bebeagua.domain.repository.IntakeRepository
import com.jjrapps.bebeagua.domain.repository.ReminderScheduler
import com.jjrapps.bebeagua.domain.repository.SettingsRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class ScheduleRemindersUseCaseTest {

    private val zone: ZoneId = ZoneId.of("Europe/Madrid")
    private val today: LocalDate = LocalDate.of(2026, 7, 24)

    private val settingsRepository = mockk<SettingsRepository>()
    private val intakeRepository = mockk<IntakeRepository>()
    private val calculateReminderTimes = CalculateReminderTimesUseCase()
    private val reminderScheduler = mockk<ReminderScheduler>(relaxed = true)

    private fun useCaseAt(time: LocalTime) = ScheduleRemindersUseCase(
        settingsRepository,
        intakeRepository,
        calculateReminderTimes,
        reminderScheduler,
        Clock.fixed(today.atTime(time).atZone(zone).toInstant(), zone)
    )

    @Test
    fun `schedules tomorrow when daily goal is already reached`() = runTest {
        every { settingsRepository.observeSettings() } returns flowOf(defaultSettings(goal = 1500))
        every { intakeRepository.observeTotalForDate(today) } returns flowOf(1500)

        useCaseAt(LocalTime.of(10, 0))()

        verify(exactly = 0) { reminderScheduler.cancel() }
        verify(exactly = 1) { reminderScheduler.scheduleNext(any(), any()) }
    }

    @Test
    fun `schedules tomorrow when consumed exceeds goal`() = runTest {
        every { settingsRepository.observeSettings() } returns flowOf(defaultSettings(goal = 1500))
        every { intakeRepository.observeTotalForDate(today) } returns flowOf(2000)

        useCaseAt(LocalTime.of(10, 0))()

        verify(exactly = 0) { reminderScheduler.cancel() }
        verify(exactly = 1) { reminderScheduler.scheduleNext(any(), any()) }
    }

    @Test
    fun `cancels when reminders count is zero`() = runTest {
        // reminders = 0 → CalculateReminderTimesUseCase returns empty list → cancel
        every { settingsRepository.observeSettings() } returns flowOf(defaultSettings(reminders = 0))
        every { intakeRepository.observeTotalForDate(today) } returns flowOf(0)

        useCaseAt(LocalTime.of(10, 0))()

        verify(exactly = 1) { reminderScheduler.cancel() }
        verify(exactly = 0) { reminderScheduler.scheduleNext(any(), any()) }
    }

    @Test
    fun `schedules the imminent slot when the grace window is disabled`() = runTest {
        givenQuarterHourlySchedule(skipImminent = false)
        givenIntakeAt(LocalTime.of(10, 58))

        useCaseAt(LocalTime.of(10, 58))()

        verify(exactly = 1) { reminderScheduler.scheduleNext(triggerAt(LocalTime.of(11, 0)), any()) }
    }

    @Test
    fun `skips the imminent slot when it falls inside the grace window`() = runTest {
        givenQuarterHourlySchedule(skipImminent = true, windowMinutes = 15)
        givenIntakeAt(LocalTime.of(10, 58))

        useCaseAt(LocalTime.of(10, 58))()

        verify(exactly = 1) { reminderScheduler.scheduleNext(triggerAt(LocalTime.of(11, 15)), any()) }
    }

    @Test
    fun `keeps the next slot when it falls outside the grace window`() = runTest {
        givenQuarterHourlySchedule(skipImminent = true, windowMinutes = 15)
        givenIntakeAt(LocalTime.of(10, 30))

        useCaseAt(LocalTime.of(10, 46))()

        verify(exactly = 1) { reminderScheduler.scheduleNext(triggerAt(LocalTime.of(11, 0)), any()) }
    }

    @Test
    fun `skips several slots when the grace window is longer than the interval`() = runTest {
        givenQuarterHourlySchedule(skipImminent = true, windowMinutes = 45)
        givenIntakeAt(LocalTime.of(10, 58))

        useCaseAt(LocalTime.of(10, 58))()

        verify(exactly = 1) { reminderScheduler.scheduleNext(triggerAt(LocalTime.of(11, 45)), any()) }
    }

    @Test
    fun `falls back to tomorrow when the grace window runs past the last slot`() = runTest {
        givenQuarterHourlySchedule(skipImminent = true, windowMinutes = 30)
        givenIntakeAt(LocalTime.of(22, 50))

        useCaseAt(LocalTime.of(22, 50))()

        val tomorrowFirstSlot = today.plusDays(1)
            .atTime(LocalTime.of(8, 0))
            .atZone(zone)
            .toInstant()
            .toEpochMilli()
        verify(exactly = 1) { reminderScheduler.scheduleNext(tomorrowFirstSlot, any()) }
    }

    @Test
    fun `snooze wins over the grace window when it is longer`() = runTest {
        givenQuarterHourlySchedule(skipImminent = true, windowMinutes = 15)
        givenIntakeAt(LocalTime.of(10, 58))

        useCaseAt(LocalTime.of(10, 58))(postponeMinutes = 30)

        verify(exactly = 1) { reminderScheduler.scheduleNext(triggerAt(LocalTime.of(11, 30)), any()) }
    }

    private fun givenQuarterHourlySchedule(skipImminent: Boolean, windowMinutes: Int = 15) {
        // 08:00–23:00 split into 61 points → one reminder every 15 minutes
        every { settingsRepository.observeSettings() } returns flowOf(
            defaultSettings(goal = 2000, reminders = 61).copy(
                dayStartMinutes = 8 * 60,
                dayEndMinutes = 23 * 60,
                skipImminentReminder = skipImminent,
                skipImminentWindowMinutes = windowMinutes
            )
        )
        every { intakeRepository.observeTotalForDate(today) } returns flowOf(200)
    }

    private fun givenIntakeAt(time: LocalTime) {
        every { intakeRepository.observeIntakesForDate(today) } returns flowOf(
            listOf(
                Intake(
                    id = 1L,
                    amountMl = 200,
                    timestampEpochMs = today.atTime(time).atZone(zone).toInstant().toEpochMilli(),
                    timezoneId = zone.id,
                    localDate = today
                )
            )
        )
    }

    private fun triggerAt(time: LocalTime): Long =
        today.atTime(time).atZone(zone).toInstant().toEpochMilli()

    private fun defaultSettings(goal: Int = 1500, reminders: Int = 6) = AppSettings(
        dailyGoalMl = goal,
        dayStartMinutes = 0,
        dayEndMinutes = 1439,
        remindersPerDay = reminders,
        intakeSizesMl = listOf(200, 300, 500),
        language = "auto"
    )
}
