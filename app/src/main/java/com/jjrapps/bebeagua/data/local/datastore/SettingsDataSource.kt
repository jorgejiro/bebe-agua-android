package com.jjrapps.bebeagua.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
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
        private val KEY_ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        private val KEY_SKIP_IMMINENT_REMINDER = booleanPreferencesKey("skip_imminent_reminder")
        private val KEY_SKIP_IMMINENT_WINDOW_MINUTES = intPreferencesKey("skip_imminent_window_minutes")

        const val DEFAULT_DAILY_GOAL_ML = 2100
        const val DEFAULT_DAY_START_MINUTES = 480   // 08:00
        const val DEFAULT_DAY_END_MINUTES = 1380    // 23:00
        const val DEFAULT_REMINDERS_PER_DAY = 10
        val DEFAULT_INTAKE_SIZES_ML = listOf(200)
        const val DEFAULT_LANGUAGE = "auto"
        const val DEFAULT_SKIP_IMMINENT_REMINDER = false
        const val DEFAULT_SKIP_IMMINENT_WINDOW_MINUTES = 15
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

    val skipImminentReminder: Flow<Boolean> =
        dataStore.data.map { it[KEY_SKIP_IMMINENT_REMINDER] ?: DEFAULT_SKIP_IMMINENT_REMINDER }

    val skipImminentWindowMinutes: Flow<Int> =
        dataStore.data.map {
            it[KEY_SKIP_IMMINENT_WINDOW_MINUTES] ?: DEFAULT_SKIP_IMMINENT_WINDOW_MINUTES
        }

    val snapshot: Flow<SettingsSnapshot> = dataStore.data.map { prefs ->
        SettingsSnapshot(
            dailyGoalMl = prefs[KEY_DAILY_GOAL_ML] ?: DEFAULT_DAILY_GOAL_ML,
            dayStartMinutes = prefs[KEY_DAY_START_MINUTES] ?: DEFAULT_DAY_START_MINUTES,
            dayEndMinutes = prefs[KEY_DAY_END_MINUTES] ?: DEFAULT_DAY_END_MINUTES,
            remindersPerDay = prefs[KEY_REMINDERS_PER_DAY] ?: DEFAULT_REMINDERS_PER_DAY,
            intakeSizesMl = prefs[KEY_INTAKE_SIZES_ML]
                ?.split(",")
                ?.mapNotNull(String::toIntOrNull)
                ?.takeIf { it.isNotEmpty() }
                ?: DEFAULT_INTAKE_SIZES_ML,
            language = prefs[KEY_LANGUAGE] ?: DEFAULT_LANGUAGE,
            skipImminentReminder = prefs[KEY_SKIP_IMMINENT_REMINDER]
                ?: DEFAULT_SKIP_IMMINENT_REMINDER,
            skipImminentWindowMinutes = prefs[KEY_SKIP_IMMINENT_WINDOW_MINUTES]
                ?: DEFAULT_SKIP_IMMINENT_WINDOW_MINUTES
        )
    }

    data class SettingsSnapshot(
        val dailyGoalMl: Int,
        val dayStartMinutes: Int,
        val dayEndMinutes: Int,
        val remindersPerDay: Int,
        val intakeSizesMl: List<Int>,
        val language: String,
        val skipImminentReminder: Boolean,
        val skipImminentWindowMinutes: Int
    )

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

    suspend fun setSkipImminentReminder(enabled: Boolean) {
        dataStore.edit { it[KEY_SKIP_IMMINENT_REMINDER] = enabled }
    }

    suspend fun setSkipImminentWindowMinutes(minutes: Int) {
        dataStore.edit { it[KEY_SKIP_IMMINENT_WINDOW_MINUTES] = minutes }
    }

    val isOnboardingDone: Flow<Boolean> =
        dataStore.data.map { it[KEY_ONBOARDING_DONE] ?: false }

    suspend fun setOnboardingDone() {
        dataStore.edit { it[KEY_ONBOARDING_DONE] = true }
    }
}
