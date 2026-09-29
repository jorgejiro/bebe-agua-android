package com.jjrapps.bebeagua.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jjrapps.bebeagua.domain.repository.SettingsRepository
import com.jjrapps.bebeagua.domain.usecase.ScheduleDailySummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    val isOnboardingDone: StateFlow<Boolean?> = settingsRepository.isOnboardingDone()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
