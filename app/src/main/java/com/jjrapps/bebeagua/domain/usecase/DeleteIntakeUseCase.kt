package com.jjrapps.bebeagua.domain.usecase

import com.jjrapps.bebeagua.domain.repository.IntakeRepository
import com.jjrapps.bebeagua.domain.repository.WidgetUpdater
import javax.inject.Inject

class DeleteIntakeUseCase @Inject constructor(
    private val intakeRepository: IntakeRepository,
    private val widgetUpdater: WidgetUpdater
) {
    suspend operator fun invoke(id: Long) {
        intakeRepository.deleteIntake(id)
        widgetUpdater.refresh()
    }
}
