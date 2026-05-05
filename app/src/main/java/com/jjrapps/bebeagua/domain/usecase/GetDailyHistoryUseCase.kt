package com.jjrapps.bebeagua.domain.usecase

import com.jjrapps.bebeagua.domain.model.DayHistory
import com.jjrapps.bebeagua.domain.repository.IntakeRepository
import com.jjrapps.bebeagua.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

class GetDailyHistoryUseCase @Inject constructor(
    private val intakeRepository: IntakeRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(from: LocalDate, to: LocalDate): List<DayHistory> {
        val goalMl = settingsRepository.observeSettings().first().dailyGoalMl
        val totals = intakeRepository.getDailyTotals(from, to)
        return generateSequence(to) { it.minusDays(1) }
            .takeWhile { !it.isBefore(from) }
            .map { date -> DayHistory(date = date, totalMl = totals[date] ?: 0, goalMl = goalMl) }
            .toList()
    }
}
