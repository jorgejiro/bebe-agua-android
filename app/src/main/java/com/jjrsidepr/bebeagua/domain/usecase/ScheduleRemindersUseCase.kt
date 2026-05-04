package com.jjrsidepr.bebeagua.domain.usecase

import com.jjrsidepr.bebeagua.domain.repository.IntakeRepository
import com.jjrsidepr.bebeagua.domain.repository.ReminderScheduler
import com.jjrsidepr.bebeagua.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

class ScheduleRemindersUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val intakeRepository: IntakeRepository,
    private val calculateReminderTimes: CalculateReminderTimesUseCase,
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke(postponeMinutes: Long = 0L) {
        val settings = settingsRepository.observeSettings().first()
        val today = LocalDate.now()
        val consumedMl = intakeRepository.observeTotalForDate(today).first()

        if (consumedMl >= settings.dailyGoalMl) {
            reminderScheduler.cancel()
            return
        }

        val now = LocalTime.now().plusMinutes(postponeMinutes)

        if (now >= settings.dayEnd) {
            reminderScheduler.cancel()
            return
        }

        val reminderTimes = calculateReminderTimes(
            settings.dayStartMinutes,
            settings.dayEndMinutes,
            settings.remindersPerDay
        )

        val nextTime = reminderTimes.firstOrNull { it > now } ?: return

        val triggerMs = today
            .atTime(nextTime)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val suggestedAmount = settings.intakeSizesMl
            .minByOrNull { Math.abs(it - settings.dailyGoalMl / settings.remindersPerDay) }
            ?: settings.intakeSizesMl.first()

        reminderScheduler.scheduleNext(triggerMs, suggestedAmount)
    }
}
