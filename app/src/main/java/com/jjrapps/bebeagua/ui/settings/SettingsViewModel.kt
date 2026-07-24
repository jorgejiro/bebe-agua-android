package com.jjrapps.bebeagua.ui.settings

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jjrapps.bebeagua.domain.repository.SettingsRepository
import com.jjrapps.bebeagua.domain.usecase.CalculateReminderTimesUseCase
import com.jjrapps.bebeagua.domain.usecase.ScheduleRemindersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val calculateReminderTimesUseCase: CalculateReminderTimesUseCase,
    private val scheduleRemindersUseCase: ScheduleRemindersUseCase
) : ViewModel() {

    private val _events = Channel<SettingsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val permissionsFlow = MutableStateFlow(
        areNotificationsGranted() to canScheduleExactAlarms()
    )

    fun refreshPermissions() {
        permissionsFlow.value = areNotificationsGranted() to canScheduleExactAlarms()
    }

    val uiState = combine(settingsRepository.observeSettings(), permissionsFlow) { settings, (notifGranted, alarmGranted) ->
        val times = calculateReminderTimesUseCase(
            settings.dayStartMinutes,
            settings.dayEndMinutes,
            settings.remindersPerDay
        )
        SettingsUiState.Success(
            settings = settings,
            calculatedTimes = times,
            notificationsGranted = notifGranted,
            exactAlarmsGranted = alarmGranted
        )
    }
        .catch<SettingsUiState> { emit(SettingsUiState.Error(it.message ?: "Error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState.Loading)

    fun updateDailyGoal(ml: Int) = update { settingsRepository.updateDailyGoal(ml) }

    fun updateDayWindow(startMinutes: Int, endMinutes: Int) = update {
        settingsRepository.updateDayWindow(startMinutes, endMinutes)
        scheduleRemindersUseCase()
    }

    fun updateRemindersPerDay(count: Int) = update {
        settingsRepository.updateRemindersPerDay(count)
        scheduleRemindersUseCase()
    }

    fun updateIntakeSizes(sizes: List<Int>) = update { settingsRepository.updateIntakeSizes(sizes) }

    fun updateSkipImminentReminder(enabled: Boolean) = update {
        settingsRepository.updateSkipImminentReminder(enabled)
        scheduleRemindersUseCase()
    }

    fun updateSkipImminentWindowMinutes(minutes: Int) = update {
        settingsRepository.updateSkipImminentWindowMinutes(minutes)
        scheduleRemindersUseCase()
    }

    fun updateLanguage(language: String) = update {
        settingsRepository.updateLanguage(language)
        val localeList = if (language == "auto") {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(language)
        }
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    private fun update(block: suspend () -> Unit) {
        viewModelScope.launch {
            runCatching { block() }
                .onFailure { _events.send(SettingsEvent.Error(it.message ?: "Error")) }
        }
    }

    private fun areNotificationsGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.getSystemService(NotificationManager::class.java)
                ?.areNotificationsEnabled() != false
        } else true
    }

    private fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(AlarmManager::class.java)
                ?.canScheduleExactAlarms() == true
        } else true
    }
}
