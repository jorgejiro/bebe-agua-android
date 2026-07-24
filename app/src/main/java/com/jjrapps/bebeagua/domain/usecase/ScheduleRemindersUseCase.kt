package com.jjrapps.bebeagua.domain.usecase

import com.jjrapps.bebeagua.domain.repository.IntakeRepository
import com.jjrapps.bebeagua.domain.repository.ReminderScheduler
import com.jjrapps.bebeagua.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class ScheduleRemindersUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val intakeRepository: IntakeRepository,
    private val calculateReminderTimes: CalculateReminderTimesUseCase,
    private val reminderScheduler: ReminderScheduler,
    private val clock: Clock
) {
    /**
     * Schedules the next reminder. Idempotent: the result only depends on the current settings,
     * today's intakes and the clock, so any caller (UI, receivers, boot) converges on the same slot.
     *
     * @param postponeMinutes delay applied when the user snoozes a reminder.
     */
    suspend operator fun invoke(postponeMinutes: Long = 0L) {
        val settings = settingsRepository.observeSettings().first()
        val today = LocalDate.now(clock)
        val consumedMl = intakeRepository.observeTotalForDate(today).first()

        val reminderTimes = calculateReminderTimes(
            settings.dayStartMinutes,
            settings.dayEndMinutes,
            settings.remindersPerDay
        )

        if (reminderTimes.isEmpty()) {
            reminderScheduler.cancel()
            return
        }

        val suggestedAmount = settings.intakeSizesMl
            .minByOrNull { Math.abs(it - settings.dailyGoalMl / settings.remindersPerDay) }
            ?: settings.intakeSizesMl.first()

        if (consumedMl >= settings.dailyGoalMl) {
            scheduleTomorrow(today, reminderTimes, suggestedAmount)
            return
        }

        val now = LocalTime.now(clock)
        val snoozeCutoff = reminderCutoff(now, postponeMinutes)
        val graceCutoff = if (settings.skipImminentReminder) {
            graceWindowCutoff(now, lastIntakeTimeToday(today), settings.skipImminentWindowMinutes)
        } else {
            now
        }
        val cutoff = if (snoozeCutoff == null || graceCutoff == null) {
            null
        } else {
            maxOf(snoozeCutoff, graceCutoff)
        }
        val nextTime = cutoff?.let { limit -> reminderTimes.firstOrNull { it > limit } }

        if (nextTime == null) {
            // No more slots today: keep the chain alive for tomorrow
            scheduleTomorrow(today, reminderTimes, suggestedAmount)
            return
        }

        val triggerMs = today
            .atTime(nextTime)
            .atZone(clock.zone)
            .toInstant()
            .toEpochMilli()

        reminderScheduler.scheduleNext(triggerMs, suggestedAmount)
    }

    private suspend fun lastIntakeTimeToday(today: LocalDate): LocalTime? =
        intakeRepository.observeIntakesForDate(today).first()
            .maxOfOrNull { it.timestampEpochMs }
            ?.let { Instant.ofEpochMilli(it).atZone(clock.zone).toLocalTime() }

    private fun scheduleTomorrow(today: LocalDate, reminderTimes: List<LocalTime>, suggestedAmountMl: Int) {
        val triggerMs = today.plusDays(1)
            .atTime(reminderTimes.first())
            .atZone(clock.zone)
            .toInstant()
            .toEpochMilli()
        reminderScheduler.scheduleNext(triggerMs, suggestedAmountMl)
    }
}
