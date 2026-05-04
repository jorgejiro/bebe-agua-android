package com.jjrsidepr.bebeagua.domain.model

import java.time.LocalDate

data class DaySummary(
    val date: LocalDate,
    val consumedMl: Int,
    val goalMl: Int,
    val intakes: List<Intake>
) {
    val progressFraction: Float get() = if (goalMl > 0) consumedMl / goalMl.toFloat() else 0f
    val isGoalReached: Boolean get() = consumedMl >= goalMl
}
