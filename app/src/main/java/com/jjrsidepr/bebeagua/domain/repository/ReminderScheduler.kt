package com.jjrsidepr.bebeagua.domain.repository

interface ReminderScheduler {
    fun scheduleNext(triggerAtMs: Long, suggestedAmountMl: Int)
    fun cancel()
}
