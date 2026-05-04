package com.jjrsidepr.bebeagua.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jjrsidepr.bebeagua.domain.usecase.AddIntakeUseCase
import com.jjrsidepr.bebeagua.domain.usecase.CalculateReminderTimesUseCase
import com.jjrsidepr.bebeagua.domain.usecase.DeleteIntakeUseCase
import com.jjrsidepr.bebeagua.domain.usecase.GetTodaySummaryUseCase
import com.jjrsidepr.bebeagua.domain.usecase.ObserveSettingsUseCase
import com.jjrsidepr.bebeagua.domain.usecase.ScheduleRemindersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getTodaySummaryUseCase: GetTodaySummaryUseCase,
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val addIntakeUseCase: AddIntakeUseCase,
    private val deleteIntakeUseCase: DeleteIntakeUseCase,
    private val scheduleRemindersUseCase: ScheduleRemindersUseCase,
    private val calculateReminderTimesUseCase: CalculateReminderTimesUseCase
) : ViewModel() {

    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    val uiState = combine(
        getTodaySummaryUseCase(),
        observeSettingsUseCase()
    ) { summary, settings ->
        val now = LocalTime.now()
        val reminderTimes = calculateReminderTimesUseCase(
            settings.dayStartMinutes,
            settings.dayEndMinutes,
            settings.remindersPerDay
        )
        val nextReminder = reminderTimes.firstOrNull { it > now }
        val defaultSize = summary.intakes.firstOrNull()?.amountMl
            ?: settings.intakeSizesMl.firstOrNull()
            ?: 200
        HomeUiState.Success(
            summary = summary,
            nextReminderTime = nextReminder,
            defaultIntakeSizeMl = defaultSize,
            availableSizesMl = settings.intakeSizesMl
        )
    }
        .catch<HomeUiState> { emit(HomeUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState.Loading)

    fun onAddIntake(amountMl: Int) {
        viewModelScope.launch {
            runCatching { addIntakeUseCase(amountMl) }
                .onSuccess {
                    scheduleRemindersUseCase()
                    _events.send(HomeEvent.IntakeAdded)
                }
                .onFailure { _events.send(HomeEvent.Error(it.message ?: "Error")) }
        }
    }

    fun onDeleteIntake(id: Long) {
        viewModelScope.launch {
            runCatching { deleteIntakeUseCase(id) }
                .onSuccess { _events.send(HomeEvent.IntakeDeleted) }
                .onFailure { _events.send(HomeEvent.Error(it.message ?: "Error")) }
        }
    }
}
