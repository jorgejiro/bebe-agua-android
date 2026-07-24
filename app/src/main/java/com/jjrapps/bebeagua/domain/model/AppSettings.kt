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

        // Single source of truth for the out-of-the-box settings: both the DataStore fallbacks
        // and the onboarding prefill read them from here so they cannot drift apart.
        const val DEFAULT_DAILY_GOAL_ML = 2400
        const val DEFAULT_DAY_START_MINUTES = 8 * 60    // 08:00
        const val DEFAULT_DAY_END_MINUTES = 21 * 60     // 21:00
        const val DEFAULT_REMINDERS_PER_DAY = 14
        val DEFAULT_INTAKE_SIZES_ML = listOf(200)
        const val DEFAULT_LANGUAGE = "auto"
        const val DEFAULT_SKIP_IMMINENT_REMINDER = false
        const val DEFAULT_SKIP_IMMINENT_WINDOW_MINUTES = 15
    }
}
