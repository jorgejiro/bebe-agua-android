package com.jjrapps.bebeagua.domain.usecase

import com.jjrapps.bebeagua.domain.repository.IntakeRepository
import javax.inject.Inject

class AddIntakeUseCase @Inject constructor(
    private val intakeRepository: IntakeRepository
) {
    suspend operator fun invoke(amountMl: Int): Long =
        intakeRepository.addIntake(amountMl)
}
