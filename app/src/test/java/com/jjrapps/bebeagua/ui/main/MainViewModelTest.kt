package com.jjrapps.bebeagua.ui.main

import com.jjrapps.bebeagua.domain.repository.SettingsRepository
import com.jjrapps.bebeagua.domain.usecase.ScheduleDailySummaryUseCase
import com.jjrapps.bebeagua.util.MainCoroutineRule
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val settingsRepository = mockk<SettingsRepository>()
    private val scheduleDailySummaryUseCase = mockk<ScheduleDailySummaryUseCase>(relaxed = true)

    private fun viewModel(onboardingDone: Boolean = true): MainViewModel {
        every { settingsRepository.isOnboardingDone() } returns flowOf(onboardingDone)
        return MainViewModel(settingsRepository, scheduleDailySummaryUseCase)
    }

    @Test
    fun `stores the date carried by the notification until it is consumed`() {
        val vm = viewModel()

        vm.onSummaryDateReceived("2026-07-24")
        assertEquals(LocalDate.of(2026, 7, 24), vm.pendingSummaryDate.value)

        vm.consumePendingSummaryDate()
        assertNull(vm.pendingSummaryDate.value)
    }

    @Test
    fun `ignores null and malformed dates`() {
        val vm = viewModel()

        vm.onSummaryDateReceived(null)
        vm.onSummaryDateReceived("garbage")

        assertNull(vm.pendingSummaryDate.value)
    }

    @Test
    fun `reschedules the daily summary on start once onboarding is done`() {
        viewModel(onboardingDone = true)

        coVerify(exactly = 1) { scheduleDailySummaryUseCase() }
    }

    @Test
    fun `does not schedule the daily summary before onboarding is done`() {
        viewModel(onboardingDone = false)

        coVerify(exactly = 0) { scheduleDailySummaryUseCase() }
    }
}
