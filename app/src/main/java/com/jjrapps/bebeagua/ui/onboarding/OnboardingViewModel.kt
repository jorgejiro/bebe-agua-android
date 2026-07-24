package com.jjrapps.bebeagua.ui.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jjrapps.bebeagua.domain.model.AppSettings
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

    var goalMl by mutableIntStateOf(AppSettings.DEFAULT_DAILY_GOAL_ML)
        private set
    var dayStartMinutes by mutableIntStateOf(AppSettings.DEFAULT_DAY_START_MINUTES)
        private set
    var dayEndMinutes by mutableIntStateOf(AppSettings.DEFAULT_DAY_END_MINUTES)
        private set
    var remindersPerDay by mutableIntStateOf(AppSettings.DEFAULT_REMINDERS_PER_DAY)
        private set

    fun setGoal(ml: Int) { goalMl = ml.coerceIn(100, 10000) }
    fun setStartMinutes(minutes: Int) { dayStartMinutes = minutes }
    fun setEndMinutes(minutes: Int) { dayEndMinutes = minutes }
    fun changeRemindersPerDay(count: Int) { remindersPerDay = count.coerceIn(1, 20) }

    fun finish() {
        viewModelScope.launch {
            settingsRepository.updateDailyGoal(goalMl)
            settingsRepository.updateDayWindow(dayStartMinutes, dayEndMinutes)
            settingsRepository.updateRemindersPerDay(remindersPerDay)
            settingsRepository.completeOnboarding()
            runCatching { scheduleRemindersUseCase() }
        }
    }
}
