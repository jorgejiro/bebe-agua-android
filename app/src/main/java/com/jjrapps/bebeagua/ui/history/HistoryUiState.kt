package com.jjrapps.bebeagua.ui.history

import com.jjrapps.bebeagua.domain.model.DayHistory

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
