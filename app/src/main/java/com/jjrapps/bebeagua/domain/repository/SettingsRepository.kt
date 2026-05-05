package com.jjrapps.bebeagua.domain.repository

import com.jjrapps.bebeagua.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>
    suspend fun updateDailyGoal(ml: Int)
    suspend fun updateDayWindow(startMinutes: Int, endMinutes: Int)
    suspend fun updateRemindersPerDay(count: Int)
    suspend fun updateIntakeSizes(sizes: List<Int>)
    suspend fun updateLanguage(language: String)
}
