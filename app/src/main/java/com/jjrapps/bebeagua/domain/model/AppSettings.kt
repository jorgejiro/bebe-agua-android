package com.jjrapps.bebeagua.domain.model

import java.time.LocalTime

data class AppSettings(
    val dailyGoalMl: Int,
    val dayStartMinutes: Int,
    val dayEndMinutes: Int,
    val remindersPerDay: Int,
    val intakeSizesMl: List<Int>,
    val language: String,
    /** When true, a reminder falling within [skipImminentWindowMinutes] after an intake is skipped. */
    val skipImminentReminder: Boolean = false,
    val skipImminentWindowMinutes: Int = 15
) {
    val dayStart: LocalTime get() = LocalTime.of(dayStartMinutes / 60, dayStartMinutes % 60)
    val dayEnd: LocalTime get() = LocalTime.of(dayEndMinutes / 60, dayEndMinutes % 60)

    companion object {
        const val MIN_SKIP_IMMINENT_WINDOW_MINUTES = 5
        const val MAX_SKIP_IMMINENT_WINDOW_MINUTES = 120
    }
}
