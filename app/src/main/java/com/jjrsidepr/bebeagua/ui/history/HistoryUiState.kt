package com.jjrsidepr.bebeagua.ui.history

import com.jjrsidepr.bebeagua.domain.model.DayHistory

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Success(
        val days: List<DayHistory>,
        val averageMl: Int,
        val bestDayMl: Int,
        val streakDays: Int
    ) : HistoryUiState
    data class Error(val message: String) : HistoryUiState
}
