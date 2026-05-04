package com.jjrsidepr.bebeagua.domain.model

import java.time.LocalDate

data class Intake(
    val id: Long,
    val amountMl: Int,
    val timestampEpochMs: Long,
    val timezoneId: String,
    val localDate: LocalDate
)
