package com.jjrapps.bebeagua.domain.usecase

import com.jjrapps.bebeagua.domain.model.AppSettings
import com.jjrapps.bebeagua.domain.repository.DailySummaryScheduler
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

class ScheduleDailySummaryUseCaseTest {

    private val zone: ZoneId = ZoneId.of("Europe/Madrid")
    private val today: LocalDate = LocalDate.of(2026, 7, 24)

    private val settingsRepository = mockk<SettingsRepository>()
    private val scheduler = mockk<DailySummaryScheduler>(relaxed = true)

    private fun useCaseAt(time: LocalTime) = ScheduleDailySummaryUseCase(
        settingsRepository,
        scheduler,
        Clock.fixed(today.atTime(time).atZone(zone).toInstant(), zone)
    )

    private fun givenSettings(enabled: Boolean = true, minutes: Int = 23 * 60) {
        every { settingsRepository.observeSettings() } returns flowOf(
            AppSettings(
                dailyGoalMl = 2400,
                dayStartMinutes = 8 * 60,
                dayEndMinutes = 21 * 60,
                remindersPerDay = 14,
                intakeSizesMl = listOf(200),
                language = "auto",
                dailySummaryEnabled = enabled,
                dailySummaryMinutes = minutes
            )
        )
    }

    private fun epochMs(date: LocalDate, time: LocalTime) =
        date.atTime(time).atZone(zone).toInstant().toEpochMilli()

    @Test
    fun `cancels when the summary is disabled`() = runTest {
        givenSettings(enabled = false)

        useCaseAt(LocalTime.of(10, 0))()

        verify(exactly = 1) { scheduler.cancel() }
        verify(exactly = 0) { scheduler.schedule(any()) }
    }

    @Test
    fun `schedules today when the configured time is still ahead`() = runTest {
        givenSettings(minutes = 23 * 60)

        useCaseAt(LocalTime.of(10, 0))()

        verify(exactly = 1) { scheduler.schedule(epochMs(today, LocalTime.of(23, 0))) }
        verify(exactly = 0) { scheduler.cancel() }
    }

    @Test
    fun `schedules tomorrow when the configured time already passed`() = runTest {
        givenSettings(minutes = 23 * 60)

        useCaseAt(LocalTime.of(23, 30))()

        verify(exactly = 1) {
            scheduler.schedule(epochMs(today.plusDays(1), LocalTime.of(23, 0)))
        }
    }

    @Test
    fun `schedules tomorrow when the configured time is exactly now`() = runTest {
        givenSettings(minutes = 23 * 60)

        useCaseAt(LocalTime.of(23, 0))()

        verify(exactly = 1) {
            scheduler.schedule(epochMs(today.plusDays(1), LocalTime.of(23, 0)))
        }
    }

    @Test
    fun `honours a custom time of day`() = runTest {
        givenSettings(minutes = 21 * 60 + 45)

        useCaseAt(LocalTime.of(9, 0))()

        verify(exactly = 1) { scheduler.schedule(epochMs(today, LocalTime.of(21, 45))) }
    }
}
