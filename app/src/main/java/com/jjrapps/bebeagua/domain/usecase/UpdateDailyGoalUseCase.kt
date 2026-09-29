package com.jjrapps.bebeagua.domain.usecase

import com.jjrapps.bebeagua.domain.repository.SettingsRepository
import com.jjrapps.bebeagua.domain.repository.WidgetUpdater
import javax.inject.Inject

/** Saves the daily goal and redraws the widgets that show it. */
class UpdateDailyGoalUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val widgetUpdater: WidgetUpdater
) {
    suspend operator fun invoke(ml: Int) {
        settingsRepository.updateDailyGoal(ml)
        widgetUpdater.refresh()
    }
}
