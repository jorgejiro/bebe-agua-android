package com.jjrapps.bebeagua.domain.usecase

import com.jjrapps.bebeagua.domain.repository.DailySummaryScheduler
import com.jjrapps.bebeagua.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class ScheduleDailySummaryUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val dailySummaryScheduler: DailySummaryScheduler,
    private val clock: Clock
) {
    /**
     * Schedules the next end-of-day summary, or cancels it when disabled. Idempotent: the result
     * only depends on the settings and the clock, so any caller converges on the same slot.
     * The next occurrence is strictly after now: today if the time is still ahead, else tomorrow.
     */
    suspend operator fun invoke() {
        val settings = settingsRepository.observeSettings().first()
        if (!settings.dailySummaryEnabled) {
            dailySummaryScheduler.cancel()
            return
        }

        val today = LocalDate.now(clock)
        val time = LocalTime.of(settings.dailySummaryMinutes / 60, settings.dailySummaryMinutes % 60)
        val now = clock.instant()
        var trigger = today.atTime(time).atZone(clock.zone)
        if (!trigger.toInstant().isAfter(now)) {
            trigger = today.plusDays(1).atTime(time).atZone(clock.zone)
        }
        dailySummaryScheduler.schedule(trigger.toInstant().toEpochMilli())
    }
}
