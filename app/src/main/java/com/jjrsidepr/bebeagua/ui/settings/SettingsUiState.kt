package com.jjrsidepr.bebeagua.ui.settings

import com.jjrsidepr.bebeagua.domain.model.AppSettings
import java.time.LocalTime

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(
        val settings: AppSettings,
        val calculatedTimes: List<LocalTime>,
        val notificationsGranted: Boolean,
        val exactAlarmsGranted: Boolean
    ) : SettingsUiState
    data class Error(val message: String) : SettingsUiState
}

sealed interface SettingsEvent {
    data object ReminderRescheduled : SettingsEvent
    data class Error(val message: String) : SettingsEvent
}
