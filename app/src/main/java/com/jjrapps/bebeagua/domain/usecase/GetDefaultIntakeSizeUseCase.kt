package com.jjrapps.bebeagua.domain.usecase

import com.jjrapps.bebeagua.domain.repository.IntakeRepository
import com.jjrapps.bebeagua.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * One-shot read of the current default intake amount, for callers outside the UI (the home screen
 * widget) that need the value at the moment of the tap instead of observing it.
 */
class GetDefaultIntakeSizeUseCase @Inject constructor(
    private val intakeRepository: IntakeRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(): Int = resolveDefaultIntakeSize(
        lastUsedMl = intakeRepository.getLastIntakeSizeMl(),
        configuredSizesMl = settingsRepository.observeSettings().first().intakeSizesMl
    )
}
