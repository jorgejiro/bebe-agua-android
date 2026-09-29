package com.jjrapps.bebeagua.domain.usecase

import com.jjrapps.bebeagua.domain.model.DaySummary
import com.jjrapps.bebeagua.domain.repository.IntakeRepository
import com.jjrapps.bebeagua.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject

/** Live summary (total, goal, intakes) of an arbitrary local date. */
class GetDaySummaryUseCase @Inject constructor(
    private val intakeRepository: IntakeRepository,
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(date: LocalDate): Flow<DaySummary> =
        combine(
            intakeRepository.observeIntakesForDate(date),
            settingsRepository.observeSettings()
        ) { intakes, settings ->
            DaySummary(
                date = date,
                consumedMl = intakes.sumOf { it.amountMl },
                goalMl = settings.dailyGoalMl,
                intakes = intakes
            )
        }
}
