package com.jjrapps.bebeagua.domain.usecase

import com.jjrapps.bebeagua.domain.repository.IntakeRepository
import com.jjrapps.bebeagua.domain.repository.ReminderScheduler
import com.jjrapps.bebeagua.domain.repository.SettingsRepository
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

        val now = LocalTime.now().plusMinutes(postponeMinutes)
        val nextTime = reminderTimes.firstOrNull { it > now }

        if (nextTime == null) {
            // No more slots today: keep the chain alive for tomorrow
            scheduleTomorrow(today, reminderTimes, suggestedAmount)
            return
        }

        val triggerMs = today
            .atTime(nextTime)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        reminderScheduler.scheduleNext(triggerMs, suggestedAmount)
    }

    private fun scheduleTomorrow(today: LocalDate, reminderTimes: List<LocalTime>, suggestedAmountMl: Int) {
        val triggerMs = today.plusDays(1)
            .atTime(reminderTimes.first())
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        reminderScheduler.scheduleNext(triggerMs, suggestedAmountMl)
    }
}
