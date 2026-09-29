package com.jjrapps.bebeagua.di

import com.jjrapps.bebeagua.data.repository.IntakeRepositoryImpl
import com.jjrapps.bebeagua.data.repository.SettingsRepositoryImpl
import com.jjrapps.bebeagua.domain.repository.DailySummaryScheduler
import com.jjrapps.bebeagua.domain.repository.IntakeRepository
import com.jjrapps.bebeagua.domain.repository.ReminderScheduler
import com.jjrapps.bebeagua.domain.repository.SettingsRepository
import com.jjrapps.bebeagua.domain.repository.WidgetUpdater
import com.jjrapps.bebeagua.reminder.AlarmManagerDailySummaryScheduler
import com.jjrapps.bebeagua.reminder.AlarmManagerReminderScheduler
import com.jjrapps.bebeagua.widget.GlanceWidgetUpdater
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindIntakeRepository(impl: IntakeRepositoryImpl): IntakeRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindReminderScheduler(impl: AlarmManagerReminderScheduler): ReminderScheduler

    @Binds
    @Singleton
    abstract fun bindDailySummaryScheduler(
        impl: AlarmManagerDailySummaryScheduler
    ): DailySummaryScheduler

    @Binds
    @Singleton
    abstract fun bindWidgetUpdater(impl: GlanceWidgetUpdater): WidgetUpdater
}
