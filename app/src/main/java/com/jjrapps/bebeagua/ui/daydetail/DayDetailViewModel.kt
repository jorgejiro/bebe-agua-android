package com.jjrapps.bebeagua.ui.daydetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jjrapps.bebeagua.domain.usecase.DeleteIntakeUseCase
import com.jjrapps.bebeagua.domain.usecase.GetDaySummaryUseCase
import com.jjrapps.bebeagua.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DayDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getDaySummaryUseCase: GetDaySummaryUseCase,
    private val deleteIntakeUseCase: DeleteIntakeUseCase
) : ViewModel() {

    private val date: LocalDate? = savedStateHandle.get<String>(Screen.DayDetail.ARG_DATE)
        ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }

    val uiState: StateFlow<DayDetailUiState> = summaryFlow(getDaySummaryUseCase)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DayDetailUiState.Loading)

    private fun summaryFlow(getDaySummaryUseCase: GetDaySummaryUseCase): Flow<DayDetailUiState> =
        if (date == null) {
            flowOf(DayDetailUiState.Error("Invalid date"))
        } else {
            getDaySummaryUseCase(date)
                .map<_, DayDetailUiState> { DayDetailUiState.Success(it) }
                .catch { emit(DayDetailUiState.Error(it.message ?: "Error")) }
        }

    fun onDeleteIntake(id: Long) {
        viewModelScope.launch {
            runCatching { deleteIntakeUseCase(id) }
                .onFailure { Timber.e(it, "Could not delete intake $id") }
        }
    }
}
