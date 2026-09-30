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

    private val _pendingNavigation = MutableStateFlow<PendingNavigation?>(null)

    /** Destination requested by the tapped notification; null once consumed. */
    val pendingNavigation: StateFlow<PendingNavigation?> = _pendingNavigation.asStateFlow()

    /**
     * Forwards the raw notification intent values. A valid summary date wins over [openHome];
     * malformed dates are ignored.
     */
    fun onNotificationIntentReceived(summaryIsoDate: String?, openHome: Boolean) {
        val date = summaryIsoDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        when {
            date != null -> _pendingNavigation.value = PendingNavigation.DayDetail(date)
            openHome -> _pendingNavigation.value = PendingNavigation.Home
        }
    }

    fun consumePendingNavigation() {
        _pendingNavigation.value = null
    }

    val isOnboardingDone: StateFlow<Boolean?> = settingsRepository.isOnboardingDone()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}

/** Screen a tapped notification asks the nav graph to show. */
sealed interface PendingNavigation {
    data object Home : PendingNavigation
    data class DayDetail(val date: LocalDate) : PendingNavigation
}
