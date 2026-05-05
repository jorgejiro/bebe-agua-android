package com.jjrapps.bebeagua.domain.model

import java.time.LocalTime

data class AppSettings(
    val dailyGoalMl: Int,
    val dayStartMinutes: Int,
    val dayEndMinutes: Int,
    val remindersPerDay: Int,
    val intakeSizesMl: List<Int>,
    val language: String,
    val theme: String
) {
    val dayStart: LocalTime get() = LocalTime.of(dayStartMinutes / 60, dayStartMinutes % 60)
    val dayEnd: LocalTime get() = LocalTime.of(dayEndMinutes / 60, dayEndMinutes % 60)
}
