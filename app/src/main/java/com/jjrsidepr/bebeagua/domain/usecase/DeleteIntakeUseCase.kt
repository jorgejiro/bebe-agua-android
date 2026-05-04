package com.jjrsidepr.bebeagua.domain.usecase

import com.jjrsidepr.bebeagua.domain.repository.IntakeRepository
import javax.inject.Inject

class DeleteIntakeUseCase @Inject constructor(
    private val intakeRepository: IntakeRepository
) {
    suspend operator fun invoke(id: Long) = intakeRepository.deleteIntake(id)
}
