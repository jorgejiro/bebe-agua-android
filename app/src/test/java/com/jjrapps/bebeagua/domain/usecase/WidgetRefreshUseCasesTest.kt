package com.jjrapps.bebeagua.domain.usecase

import com.jjrapps.bebeagua.domain.repository.IntakeRepository
import com.jjrapps.bebeagua.domain.repository.SettingsRepository
import com.jjrapps.bebeagua.domain.repository.WidgetUpdater
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class WidgetRefreshUseCasesTest {

    private val intakeRepository = mockk<IntakeRepository>(relaxed = true)
    private val settingsRepository = mockk<SettingsRepository>(relaxed = true)
    private val widgetUpdater = mockk<WidgetUpdater>(relaxed = true)

    @Test
    fun `adding an intake saves it and then refreshes the widget`() = runTest {
        coEvery { intakeRepository.addIntake(250) } returns 9L

        val id = AddIntakeUseCase(intakeRepository, widgetUpdater)(250)

        assertEquals(9L, id)
        coVerifyOrder {
            intakeRepository.addIntake(250)
            widgetUpdater.refresh()
        }
    }

    @Test
    fun `deleting an intake removes it and then refreshes the widget`() = runTest {
        DeleteIntakeUseCase(intakeRepository, widgetUpdater)(7L)

        coVerifyOrder {
            intakeRepository.deleteIntake(7L)
            widgetUpdater.refresh()
        }
    }

    @Test
    fun `updating the goal saves it and then refreshes the widget`() = runTest {
        UpdateDailyGoalUseCase(settingsRepository, widgetUpdater)(3000)

        coVerifyOrder {
            settingsRepository.updateDailyGoal(3000)
            widgetUpdater.refresh()
        }
    }

    @Test
    fun `a failed save does not refresh the widget`() = runTest {
        coEvery { intakeRepository.addIntake(any()) } throws IllegalStateException("db")

        runCatching { AddIntakeUseCase(intakeRepository, widgetUpdater)(250) }

        coVerify(exactly = 0) { widgetUpdater.refresh() }
    }
}
