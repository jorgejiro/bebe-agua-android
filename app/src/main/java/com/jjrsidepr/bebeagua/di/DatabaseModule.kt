package com.jjrsidepr.bebeagua.di

import android.content.Context
import androidx.room.Room
import com.jjrsidepr.bebeagua.data.local.db.AppDatabase
import com.jjrsidepr.bebeagua.data.local.db.IntakeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "bebe_agua.db")
            .build()

    @Provides
    fun provideIntakeDao(db: AppDatabase): IntakeDao = db.intakeDao()
}
