package com.jjrapps.bebeagua.domain.repository

/** Owns the single alarm slot of the end-of-day summary, independent from the reminders slot. */
interface DailySummaryScheduler {
    fun schedule(triggerAtMs: Long)
    fun cancel()
}
