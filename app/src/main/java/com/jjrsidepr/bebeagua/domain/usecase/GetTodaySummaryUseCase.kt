package com.jjrsidepr.bebeagua.domain.usecase

import com.jjrsidepr.bebeagua.domain.model.DaySummary
import com.jjrsidepr.bebeagua.domain.repository.IntakeRepository
import com.jjrsidepr.bebeagua.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject

class GetTodaySummaryUseCase @Inject constructor(
    private val intakeRepository: IntakeRepository,
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<DaySummary> =
        combine(
            intakeRepository.observeIntakesForDate(LocalDate.now()),
            settingsRepository.observeSettings()
        ) { intakes, settings ->
            DaySummary(
                date = LocalDate.now(),
                consumedMl = intakes.sumOf { it.amountMl },
                goalMl = settings.dailyGoalMl,
                intakes = intakes
            )
        }
}
