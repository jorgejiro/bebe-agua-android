package com.jjrapps.bebeagua.domain.usecase

import com.jjrapps.bebeagua.domain.repository.IntakeRepository
import com.jjrapps.bebeagua.domain.repository.WidgetUpdater
import javax.inject.Inject

class AddIntakeUseCase @Inject constructor(
    private val intakeRepository: IntakeRepository,
    private val widgetUpdater: WidgetUpdater
) {
    suspend operator fun invoke(amountMl: Int): Long {
        val id = intakeRepository.addIntake(amountMl)
        widgetUpdater.refresh()
        return id
    }
}
