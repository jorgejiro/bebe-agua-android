package com.jjrsidepr.bebeagua.domain.usecase

import com.jjrsidepr.bebeagua.domain.repository.IntakeRepository
import javax.inject.Inject

class AddIntakeUseCase @Inject constructor(
    private val intakeRepository: IntakeRepository
) {
    suspend operator fun invoke(amountMl: Int): Long =
        intakeRepository.addIntake(amountMl)
}
