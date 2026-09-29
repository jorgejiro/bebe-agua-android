package com.jjrapps.bebeagua.ui.daydetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.jjrapps.bebeagua.domain.model.DaySummary
import com.jjrapps.bebeagua.domain.model.Intake
import com.jjrapps.bebeagua.domain.usecase.DeleteIntakeUseCase
import com.jjrapps.bebeagua.domain.usecase.GetDaySummaryUseCase
import com.jjrapps.bebeagua.ui.navigation.Screen
import com.jjrapps.bebeagua.util.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class DayDetailViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val date = LocalDate.of(2026, 7, 24)
    private val getDaySummaryUseCase = mockk<GetDaySummaryUseCase>()
    private val deleteIntakeUseCase = mockk<DeleteIntakeUseCase>()

    private fun viewModel(rawDate: String? = date.toString()) = DayDetailViewModel(
        SavedStateHandle(if (rawDate == null) emptyMap() else mapOf(Screen.DayDetail.ARG_DATE to rawDate)),
        getDaySummaryUseCase,
        deleteIntakeUseCase
    )

    private fun summary() = DaySummary(
        date = date,
        consumedMl = 500,
        goalMl = 2400,
        intakes = listOf(Intake(7L, 500, 1_000L, "Europe/Madrid", date))
    )

    @Test
    fun `emits Success with the summary of the requested date`() = runTest {
        every { getDaySummaryUseCase(date) } returns flowOf(summary())

        viewModel().uiState.test {
            var item = awaitItem()
            if (item == DayDetailUiState.Loading) item = awaitItem()
            assertEquals(DayDetailUiState.Success(summary()), item)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Error when the date argument is malformed`() = runTest {
        viewModel("not-a-date").uiState.test {
            var item = awaitItem()
            if (item == DayDetailUiState.Loading) item = awaitItem()
            assertTrue(item is DayDetailUiState.Error)
            cancelAndIgnoreRemainingEvents()
        }
        verify(exactly = 0) { getDaySummaryUseCase(any()) }
    }

    @Test
    fun `emits Error when the date argument is missing`() = runTest {
        viewModel(null).uiState.test {
            var item = awaitItem()
            if (item == DayDetailUiState.Loading) item = awaitItem()
            assertTrue(item is DayDetailUiState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Error when the summary flow fails`() = runTest {
        every { getDaySummaryUseCase(date) } returns flow { throw RuntimeException("db error") }

        viewModel().uiState.test {
            var item = awaitItem()
            if (item == DayDetailUiState.Loading) item = awaitItem()
            assertEquals(DayDetailUiState.Error("db error"), item)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDeleteIntake delegates to the use case`() = runTest {
        every { getDaySummaryUseCase(date) } returns flowOf(summary())
        coEvery { deleteIntakeUseCase(7L) } returns Unit

        viewModel().onDeleteIntake(7L)

        coVerify(exactly = 1) { deleteIntakeUseCase(7L) }
    }

    @Test
    fun `onDeleteIntake swallows use case failures`() = runTest {
        every { getDaySummaryUseCase(date) } returns flowOf(summary())
        coEvery { deleteIntakeUseCase(any()) } throws RuntimeException("db error")

        viewModel().onDeleteIntake(7L)

        coVerify(exactly = 1) { deleteIntakeUseCase(7L) }
    }
}
