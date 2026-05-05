package com.jjrapps.bebeagua.ui.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jjrapps.bebeagua.domain.repository.SettingsRepository
import com.jjrapps.bebeagua.domain.usecase.ScheduleRemindersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val scheduleRemindersUseCase: ScheduleRemindersUseCase
) : ViewModel() {

    var goalMl by mutableIntStateOf(1500)
        private set
    var dayStartMinutes by mutableIntStateOf(480)
        private set
    var dayEndMinutes by mutableIntStateOf(1380)
        private set

    fun setGoal(ml: Int) { goalMl = ml.coerceIn(100, 10000) }
    fun setStartMinutes(minutes: Int) { dayStartMinutes = minutes }
    fun setEndMinutes(minutes: Int) { dayEndMinutes = minutes }

    fun finish() {
        viewModelScope.launch {
            settingsRepository.updateDailyGoal(goalMl)
            settingsRepository.updateDayWindow(dayStartMinutes, dayEndMinutes)
            settingsRepository.completeOnboarding()
            runCatching { scheduleRemindersUseCase() }
        }
    }
}
