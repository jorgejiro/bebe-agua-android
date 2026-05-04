package com.jjrsidepr.bebeagua.di

import com.jjrsidepr.bebeagua.data.repository.IntakeRepositoryImpl
import com.jjrsidepr.bebeagua.data.repository.SettingsRepositoryImpl
import com.jjrsidepr.bebeagua.domain.repository.IntakeRepository
import com.jjrsidepr.bebeagua.domain.repository.ReminderScheduler
import com.jjrsidepr.bebeagua.domain.repository.SettingsRepository
import com.jjrsidepr.bebeagua.reminder.AlarmManagerReminderScheduler
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
}
