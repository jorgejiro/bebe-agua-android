package com.jjrsidepr.bebeagua.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_DAILY_GOAL_ML = intPreferencesKey("daily_goal_ml")
        private val KEY_DAY_START_MINUTES = intPreferencesKey("day_start_minutes")
        private val KEY_DAY_END_MINUTES = intPreferencesKey("day_end_minutes")
        private val KEY_REMINDERS_PER_DAY = intPreferencesKey("reminders_per_day")
        private val KEY_INTAKE_SIZES_ML = stringPreferencesKey("intake_sizes_ml")
        private val KEY_LANGUAGE = stringPreferencesKey("language")
        private val KEY_THEME = stringPreferencesKey("theme")

        const val DEFAULT_DAILY_GOAL_ML = 1500
        const val DEFAULT_DAY_START_MINUTES = 480   // 08:00
        const val DEFAULT_DAY_END_MINUTES = 1380    // 23:00
        const val DEFAULT_REMINDERS_PER_DAY = 6
        val DEFAULT_INTAKE_SIZES_ML = listOf(200)
        const val DEFAULT_LANGUAGE = "auto"
        const val DEFAULT_THEME = "auto"
    }

    val dailyGoalMl: Flow<Int> =
        dataStore.data.map { it[KEY_DAILY_GOAL_ML] ?: DEFAULT_DAILY_GOAL_ML }

    val dayStartMinutes: Flow<Int> =
        dataStore.data.map { it[KEY_DAY_START_MINUTES] ?: DEFAULT_DAY_START_MINUTES }

    val dayEndMinutes: Flow<Int> =
        dataStore.data.map { it[KEY_DAY_END_MINUTES] ?: DEFAULT_DAY_END_MINUTES }

    val remindersPerDay: Flow<Int> =
        dataStore.data.map { it[KEY_REMINDERS_PER_DAY] ?: DEFAULT_REMINDERS_PER_DAY }

    val intakeSizesMl: Flow<List<Int>> = dataStore.data.map { prefs ->
        prefs[KEY_INTAKE_SIZES_ML]
            ?.split(",")
            ?.mapNotNull(String::toIntOrNull)
            ?.takeIf { it.isNotEmpty() }
            ?: DEFAULT_INTAKE_SIZES_ML
    }

    val language: Flow<String> =
        dataStore.data.map { it[KEY_LANGUAGE] ?: DEFAULT_LANGUAGE }

    val theme: Flow<String> =
        dataStore.data.map { it[KEY_THEME] ?: DEFAULT_THEME }

    suspend fun setDailyGoalMl(value: Int) {
        dataStore.edit { it[KEY_DAILY_GOAL_ML] = value }
    }

    suspend fun setDayStartMinutes(minutes: Int) {
        dataStore.edit { it[KEY_DAY_START_MINUTES] = minutes }
    }

    suspend fun setDayEndMinutes(minutes: Int) {
        dataStore.edit { it[KEY_DAY_END_MINUTES] = minutes }
    }

    suspend fun setRemindersPerDay(count: Int) {
        dataStore.edit { it[KEY_REMINDERS_PER_DAY] = count }
    }

    suspend fun setIntakeSizesMl(sizes: List<Int>) {
        dataStore.edit { it[KEY_INTAKE_SIZES_ML] = sizes.joinToString(",") }
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { it[KEY_LANGUAGE] = language }
    }

    suspend fun setTheme(theme: String) {
        dataStore.edit { it[KEY_THEME] = theme }
    }
}
