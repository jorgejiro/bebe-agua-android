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

        vm.onNotificationIntentReceived("2026-07-24", openHome = false)
        assertEquals(PendingNavigation.DayDetail(LocalDate.of(2026, 7, 24)), vm.pendingNavigation.value)

        vm.consumePendingNavigation()
        assertNull(vm.pendingNavigation.value)
    }

    @Test
    fun `requests home when a reminder notification asks for it`() {
        val vm = viewModel()

        vm.onNotificationIntentReceived(null, openHome = true)
        assertEquals(PendingNavigation.Home, vm.pendingNavigation.value)

        vm.consumePendingNavigation()
        assertNull(vm.pendingNavigation.value)
    }

    @Test
    fun `summary date wins over the home request`() {
        val vm = viewModel()

        vm.onNotificationIntentReceived("2026-07-24", openHome = true)

        assertEquals(PendingNavigation.DayDetail(LocalDate.of(2026, 7, 24)), vm.pendingNavigation.value)
    }

    @Test
    fun `malformed date with a home request falls back to home`() {
        val vm = viewModel()

        vm.onNotificationIntentReceived("garbage", openHome = true)

        assertEquals(PendingNavigation.Home, vm.pendingNavigation.value)
    }

    @Test
    fun `requests nothing for null or malformed dates without a home request`() {
        val vm = viewModel()

        vm.onNotificationIntentReceived(null, openHome = false)
        vm.onNotificationIntentReceived("garbage", openHome = false)

        assertNull(vm.pendingNavigation.value)
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
