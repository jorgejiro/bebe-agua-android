package com.jjrapps.bebeagua.domain.usecase

import com.jjrapps.bebeagua.domain.repository.IntakeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLastIntakeSizeUseCase @Inject constructor(
    private val intakeRepository: IntakeRepository
) {
    operator fun invoke(): Flow<Int?> = intakeRepository.observeLastIntakeSizeMl()
}
