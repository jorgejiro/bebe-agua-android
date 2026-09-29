package com.jjrapps.bebeagua.ui.daydetail

import com.jjrapps.bebeagua.domain.model.DaySummary

sealed interface DayDetailUiState {
    data object Loading : DayDetailUiState
    data class Success(val summary: DaySummary) : DayDetailUiState
    data class Error(val message: String) : DayDetailUiState
}
