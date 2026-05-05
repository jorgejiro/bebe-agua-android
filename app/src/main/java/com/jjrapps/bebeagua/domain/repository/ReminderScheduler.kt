package com.jjrapps.bebeagua.domain.repository

interface ReminderScheduler {
    fun scheduleNext(triggerAtMs: Long, suggestedAmountMl: Int)
    fun cancel()
}
