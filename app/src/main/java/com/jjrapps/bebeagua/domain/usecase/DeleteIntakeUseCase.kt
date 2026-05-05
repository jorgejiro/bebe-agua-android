package com.jjrapps.bebeagua.domain.usecase

import com.jjrapps.bebeagua.domain.repository.IntakeRepository
import javax.inject.Inject

class DeleteIntakeUseCase @Inject constructor(
    private val intakeRepository: IntakeRepository
) {
    suspend operator fun invoke(id: Long) = intakeRepository.deleteIntake(id)
}
