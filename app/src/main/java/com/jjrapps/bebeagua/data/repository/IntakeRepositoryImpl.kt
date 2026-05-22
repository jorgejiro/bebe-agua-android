package com.jjrapps.bebeagua.data.repository

import com.jjrapps.bebeagua.data.local.db.IntakeDao
import com.jjrapps.bebeagua.data.local.db.IntakeEntity
import com.jjrapps.bebeagua.domain.model.Intake
import com.jjrapps.bebeagua.domain.repository.IntakeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntakeRepositoryImpl @Inject constructor(
    private val intakeDao: IntakeDao
) : IntakeRepository {

    override fun observeIntakesForDate(date: LocalDate): Flow<List<Intake>> =
        intakeDao.observeIntakesForDate(date).map { it.map(IntakeEntity::toDomain) }

    override fun observeTotalForDate(date: LocalDate): Flow<Int> =
        intakeDao.observeTotalForDate(date)

    override suspend fun addIntake(amountMl: Int): Long {
        val nowMs = System.currentTimeMillis()
        val zoneId = ZoneId.systemDefault()
        val localDate = Instant.ofEpochMilli(nowMs).atZone(zoneId).toLocalDate()
        return intakeDao.insert(
            IntakeEntity(
                amountMl = amountMl,
                timestampEpochMs = nowMs,
                timezoneId = zoneId.id,
                localDate = localDate.toString()
            )
        )
    }

    override suspend fun deleteIntake(id: Long) = intakeDao.delete(id)

    override suspend fun getDailyTotals(from: LocalDate, to: LocalDate): Map<LocalDate, Int> =
        intakeDao.getDailyTotalsBetween(from, to)
            .associate { LocalDate.parse(it.localDate) to it.totalMl }

    override suspend fun getLastIntakeSizeMl(): Int? = intakeDao.getLastIntakeSizeMl()

    override fun observeLastIntakeSizeMl(): Flow<Int?> = intakeDao.observeLastIntakeSizeMl()
}

private fun IntakeEntity.toDomain() = Intake(
    id = id,
    amountMl = amountMl,
    timestampEpochMs = timestampEpochMs,
    timezoneId = timezoneId,
    localDate = LocalDate.parse(localDate)
)
