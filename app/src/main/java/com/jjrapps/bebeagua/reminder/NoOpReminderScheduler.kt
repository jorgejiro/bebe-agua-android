package com.jjrapps.bebeagua.reminder

import com.jjrapps.bebeagua.domain.repository.ReminderScheduler
import javax.inject.Inject

// Placeholder until AlarmManager implementation is done in the reminder task.
class NoOpReminderScheduler @Inject constructor() : ReminderScheduler {
    override fun scheduleNext(triggerAtMs: Long, suggestedAmountMl: Int) = Unit
    override fun cancel() = Unit
}
