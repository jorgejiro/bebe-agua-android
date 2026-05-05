package com.jjrapps.bebeagua.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jjrapps.bebeagua.domain.model.DayHistory
import com.jjrapps.bebeagua.domain.usecase.GetDailyHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getDailyHistoryUseCase: GetDailyHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = HistoryUiState.Loading
            runCatching {
                val to = LocalDate.now()
                val from = to.minusDays(29)
                getDailyHistoryUseCase(from, to)
            }.onSuccess { days ->
                val daysWithData = days.filter { it.totalMl > 0 }
                val averageMl = if (daysWithData.isNotEmpty())
                    daysWithData.sumOf { it.totalMl } / daysWithData.size else 0
                val bestDayMl = days.maxOfOrNull { it.totalMl } ?: 0
                _uiState.value = HistoryUiState.Success(
                    days = days,
                    averageMl = averageMl,
                    bestDayMl = bestDayMl,
                    streakDays = computeStreak(days)
                )
            }.onFailure { e ->
                _uiState.value = HistoryUiState.Error(e.message ?: "Error")
            }
        }
    }

    private fun computeStreak(days: List<DayHistory>): Int {
        var streak = 0
        for (day in days) {
            if (day.isGoalReached) streak++ else break
        }
        return streak
    }
}
