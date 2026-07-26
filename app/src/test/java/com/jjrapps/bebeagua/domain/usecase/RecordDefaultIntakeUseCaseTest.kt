package com.jjrapps.bebeagua.domain.usecase

import com.jjrapps.bebeagua.domain.model.AppSettings
import com.jjrapps.bebeagua.domain.repository.IntakeRepository
import com.jjrapps.bebeagua.domain.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class RecordDefaultIntakeUseCaseTest {

    private val zone = ZoneId.of("Europe/Madrid")
    private val clock = Clock.fixed(Instant.parse("2026-07-26T10:00:00Z"), zone)
    private val today = LocalDate.now(clock)

    private val getDefaultIntakeSize = mockk<GetDefaultIntakeSizeUseCase>()
    private val addIntake = mockk<AddIntakeUseCase>(relaxed = true)
    private val scheduleReminders = mockk<ScheduleRemindersUseCase>(relaxed = true)
    private val intakeRepository = mockk<IntakeRepository>()
    private val settingsRepository = mockk<SettingsRepository>()

    private val useCase = RecordDefaultIntakeUseCase(
        getDefaultIntakeSize,
        addIntake,
        scheduleReminders,
        intakeRepository,
        settingsRepository,
        clock
    )

    @Test
    fun `logs the default amount and reschedules the next reminder`() = runTest {
        givenDefaultSize(250)
        givenDayTotals(consumedMl = 750, goalMl = 2400)

        useCase()

        coVerify(exactly = 1) { addIntake(250) }
        coVerify(exactly = 1) { scheduleReminders() }
    }

    @Test
    fun `reports the amount logged and the day totals`() = runTest {
        givenDefaultSize(330)
        givenDayTotals(consumedMl = 1330, goalMl = 2000)

        val recorded = useCase()

        assertEquals(330, recorded.amountMl)
        assertEquals(1330, recorded.consumedMl)
        assertEquals(2000, recorded.goalMl)
    }

    private fun givenDefaultSize(amountMl: Int) {
        coEvery { getDefaultIntakeSize() } returns amountMl
    }

    private fun givenDayTotals(consumedMl: Int, goalMl: Int) {
        every { intakeRepository.observeTotalForDate(today) } returns flowOf(consumedMl)
        every { settingsRepository.observeSettings() } returns flowOf(
            AppSettings(
                dailyGoalMl = goalMl,
                dayStartMinutes = 480,
                dayEndMinutes = 1260,
                remindersPerDay = 14,
                intakeSizesMl = listOf(200),
                language = "auto"
            )
        )
    }
}
