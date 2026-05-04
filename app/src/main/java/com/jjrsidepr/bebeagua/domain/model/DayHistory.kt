package com.jjrsidepr.bebeagua.domain.model

import java.time.LocalDate

data class DayHistory(
    val date: LocalDate,
    val totalMl: Int,
    val goalMl: Int
) {
    val progressFraction: Float get() = if (goalMl > 0) totalMl / goalMl.toFloat() else 0f
    val isGoalReached: Boolean get() = totalMl >= goalMl
}
