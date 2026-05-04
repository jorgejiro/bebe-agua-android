package com.jjrsidepr.bebeagua.data.repository

import com.jjrsidepr.bebeagua.data.local.datastore.SettingsDataSource
import com.jjrsidepr.bebeagua.domain.model.AppSettings
import com.jjrsidepr.bebeagua.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataSource: SettingsDataSource
) : SettingsRepository {

    override fun observeSettings(): Flow<AppSettings> =
        dataSource.snapshot.map { s ->
            AppSettings(
                dailyGoalMl = s.dailyGoalMl,
                dayStartMinutes = s.dayStartMinutes,
                dayEndMinutes = s.dayEndMinutes,
                remindersPerDay = s.remindersPerDay,
                intakeSizesMl = s.intakeSizesMl,
                language = s.language,
                theme = s.theme
            )
        }

    override suspend fun updateDailyGoal(ml: Int) = dataSource.setDailyGoalMl(ml)

    override suspend fun updateDayWindow(startMinutes: Int, endMinutes: Int) {
        dataSource.setDayStartMinutes(startMinutes)
        dataSource.setDayEndMinutes(endMinutes)
    }

    override suspend fun updateRemindersPerDay(count: Int) = dataSource.setRemindersPerDay(count)

    override suspend fun updateIntakeSizes(sizes: List<Int>) = dataSource.setIntakeSizesMl(sizes)

    override suspend fun updateLanguage(language: String) = dataSource.setLanguage(language)

    override suspend fun updateTheme(theme: String) = dataSource.setTheme(theme)
}
