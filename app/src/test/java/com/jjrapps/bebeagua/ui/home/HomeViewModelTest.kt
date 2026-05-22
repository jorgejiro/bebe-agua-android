package com.jjrapps.bebeagua.ui.home

import app.cash.turbine.test
import com.jjrapps.bebeagua.domain.model.AppSettings
import com.jjrapps.bebeagua.domain.model.DaySummary
import com.jjrapps.bebeagua.domain.usecase.AddIntakeUseCase
import com.jjrapps.bebeagua.domain.usecase.CalculateReminderTimesUseCase
import com.jjrapps.bebeagua.domain.usecase.DeleteIntakeUseCase
import com.jjrapps.bebeagua.domain.usecase.GetTodaySummaryUseCase
import com.jjrapps.bebeagua.domain.usecase.ObserveLastIntakeSizeUseCase
import com.jjrapps.bebeagua.domain.usecase.ObserveSettingsUseCase
import com.jjrapps.bebeagua.domain.usecase.ScheduleRemindersUseCase
import com.jjrapps.bebeagua.util.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val getTodaySummaryUseCase = mockk<GetTodaySummaryUseCase>()
    private val observeSettingsUseCase = mockk<ObserveSettingsUseCase>()
    private val observeLastIntakeSizeUseCase = mockk<ObserveLastIntakeSizeUseCase>()
    private val addIntakeUseCase = mockk<AddIntakeUseCase>()
    private val deleteIntakeUseCase = mockk<DeleteIntakeUseCase>()
    private val scheduleRemindersUseCase = mockk<ScheduleRemindersUseCase>(relaxed = true)
    private val calculateReminderTimesUseCase = CalculateReminderTimesUseCase()

    private fun buildViewModel(): HomeViewModel {
        every { getTodaySummaryUseCase() } returns flowOf(defaultSummary())
        every { observeSettingsUseCase() } returns flowOf(defaultSettings())
        every { observeLastIntakeSizeUseCase() } returns flowOf(null)
        return HomeViewModel(
            getTodaySummaryUseCase,
            observeSettingsUseCase,
            observeLastIntakeSizeUseCase,
            addIntakeUseCase,
            deleteIntakeUseCase,
            scheduleRemindersUseCase,
            calculateReminderTimesUseCase
        )
    }

    @Test
    fun `initial uiState value is Loading before any subscriber`() {
        val viewModel = buildViewModel()
        assertEquals(HomeUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState emits Success with correct consumed amount`() = runTest {
        every { getTodaySummaryUseCase() } returns flowOf(defaultSummary(consumed = 600))
        every { observeSettingsUseCase() } returns flowOf(defaultSettings())
        every { observeLastIntakeSizeUseCase() } returns flowOf(null)
        val viewModel = HomeViewModel(
            getTodaySummaryUseCase, observeSettingsUseCase, observeLastIntakeSizeUseCase, addIntakeUseCase,
            deleteIntakeUseCase, scheduleRemindersUseCase, calculateReminderTimesUseCase
        )

        viewModel.uiState.test {
            // StateFlow replays current value (Loading), then upstream emits Success
            val first = awaitItem()
            val success = if (first is HomeUiState.Success) first else awaitItem() as HomeUiState.Success
            assertEquals(600, success.summary.consumedMl)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState uses latest intake amount as default size`() = runTest {
        every { getTodaySummaryUseCase() } returns flowOf(defaultSummary())
        every { observeSettingsUseCase() } returns flowOf(defaultSettings())
        every { observeLastIntakeSizeUseCase() } returns flowOf(450)
        val viewModel = HomeViewModel(
            getTodaySummaryUseCase, observeSettingsUseCase, observeLastIntakeSizeUseCase, addIntakeUseCase,
            deleteIntakeUseCase, scheduleRemindersUseCase, calculateReminderTimesUseCase
        )

        viewModel.uiState.test {
            val first = awaitItem()
            val success = if (first is HomeUiState.Success) first else awaitItem() as HomeUiState.Success
            assertEquals(450, success.defaultIntakeSizeMl)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onAddIntake emits IntakeAdded event and triggers reminder scheduling`() = runTest {
        val viewModel = buildViewModel()
        coEvery { addIntakeUseCase(250) } returns 1L

        viewModel.events.test {
            viewModel.onAddIntake(250)
            assertEquals(HomeEvent.IntakeAdded, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { scheduleRemindersUseCase() }
    }

    @Test
    fun `onAddIntake emits Error event when use case throws`() = runTest {
        val viewModel = buildViewModel()
        coEvery { addIntakeUseCase(any()) } throws RuntimeException("db error")

        viewModel.events.test {
            viewModel.onAddIntake(200)
            val event = awaitItem()
            assertTrue(event is HomeEvent.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDeleteIntake emits IntakeDeleted event on success`() = runTest {
        val viewModel = buildViewModel()
        coEvery { deleteIntakeUseCase(42L) } returns Unit

        viewModel.events.test {
            viewModel.onDeleteIntake(42L)
            assertEquals(HomeEvent.IntakeDeleted, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun defaultSummary(consumed: Int = 0) = DaySummary(
        date = LocalDate.now(),
        consumedMl = consumed,
        goalMl = 1500,
        intakes = emptyList()
    )

    private fun defaultSettings() = AppSettings(
        dailyGoalMl = 1500,
        dayStartMinutes = 480,
        dayEndMinutes = 1380,
        remindersPerDay = 6,
        intakeSizesMl = listOf(200, 300, 500),
        language = "auto"
    )
}
