package com.jjrapps.bebeagua.ui.home

import com.jjrapps.bebeagua.domain.model.DaySummary
import java.time.LocalTime

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val summary: DaySummary,
        val nextReminderTime: LocalTime?,
        val defaultIntakeSizeMl: Int,
        val availableSizesMl: List<Int>
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

sealed interface HomeEvent {
    data object IntakeAdded : HomeEvent
    data object IntakeDeleted : HomeEvent
    data class Error(val message: String) : HomeEvent
}
