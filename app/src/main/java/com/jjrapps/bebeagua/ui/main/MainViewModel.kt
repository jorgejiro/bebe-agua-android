package com.jjrapps.bebeagua.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jjrapps.bebeagua.domain.repository.SettingsRepository
import com.jjrapps.bebeagua.domain.usecase.ScheduleDailySummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    settingsRepository: SettingsRepository,
    scheduleDailySummaryUseCase: ScheduleDailySummaryUseCase
) : ViewModel() {

    init {
        // Idempotent. Users who update the app already finished onboarding and would otherwise
        // never get the summary scheduled until the next boot or settings change.
        viewModelScope.launch {
            if (settingsRepository.isOnboardingDone().first()) {
                runCatching { scheduleDailySummaryUseCase() }
            }
        }
    }

    private val _pendingSummaryDate = MutableStateFlow<LocalDate?>(null)

    /** Day the user asked to open by tapping the daily summary notification; null once consumed. */
    val pendingSummaryDate: StateFlow<LocalDate?> = _pendingSummaryDate.asStateFlow()

    /** Forwards the raw ISO date carried by the notification intent; malformed values are ignored. */
    fun onSummaryDateReceived(isoDate: String?) {
        isoDate ?: return
        runCatching { LocalDate.parse(isoDate) }.getOrNull()?.let { _pendingSummaryDate.value = it }
    }

    fun consumePendingSummaryDate() {
        _pendingSummaryDate.value = null
    }

    val isOnboardingDone: StateFlow<Boolean?> = settingsRepository.isOnboardingDone()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
