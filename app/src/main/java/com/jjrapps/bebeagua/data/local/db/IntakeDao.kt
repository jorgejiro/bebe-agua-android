package com.jjrapps.bebeagua.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface IntakeDao {

    @Query("SELECT * FROM intake WHERE local_date = :date ORDER BY timestamp_epoch_ms DESC")
    fun observeIntakesForDate(date: LocalDate): Flow<List<IntakeEntity>>

    @Query("SELECT COALESCE(SUM(amount_ml), 0) FROM intake WHERE local_date = :date")
    fun observeTotalForDate(date: LocalDate): Flow<Int>

    @Query("""
        SELECT local_date, SUM(amount_ml) AS total_ml
        FROM intake
        WHERE local_date BETWEEN :start AND :end
        GROUP BY local_date
        ORDER BY local_date DESC
    """)
    suspend fun getDailyTotalsBetween(start: LocalDate, end: LocalDate): List<DayTotal>

    @Insert
    suspend fun insert(intake: IntakeEntity): Long

    @Query("DELETE FROM intake WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT amount_ml FROM intake ORDER BY timestamp_epoch_ms DESC LIMIT 1")
    suspend fun getLastIntakeSizeMl(): Int?
}
