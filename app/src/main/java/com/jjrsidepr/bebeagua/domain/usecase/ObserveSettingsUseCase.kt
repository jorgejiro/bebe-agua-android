package com.jjrsidepr.bebeagua.domain.usecase

import com.jjrsidepr.bebeagua.domain.model.AppSettings
import com.jjrsidepr.bebeagua.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<AppSettings> = settingsRepository.observeSettings()
}
