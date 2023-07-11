package com.side.runwithme.module

import android.content.Context
import androidx.room.Room
import com.side.data.db.RunDao
import com.side.data.db.RunDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object LocalDataSourceModule {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): RunDatabase = Room
        .databaseBuilder(context, RunDatabase::class.java, "practice.db")
        .build()

    @Singleton
    @Provides
    fun provideRunDao(runDatabase: RunDatabase): RunDao = runDatabase.runDao()
}