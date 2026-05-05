package com.jjrapps.bebeagua.domain.repository

import com.jjrapps.bebeagua.domain.model.Intake
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface IntakeRepository {
    fun observeIntakesForDate(date: LocalDate): Flow<List<Intake>>
    fun observeTotalForDate(date: LocalDate): Flow<Int>
    suspend fun addIntake(amountMl: Int): Long
    suspend fun deleteIntake(id: Long)
    suspend fun getDailyTotals(from: LocalDate, to: LocalDate): Map<LocalDate, Int>
    suspend fun getLastIntakeSizeMl(): Int?
}
