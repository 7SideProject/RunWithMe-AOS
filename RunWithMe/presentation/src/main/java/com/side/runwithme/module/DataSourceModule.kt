package com.side.runwithme.module

import com.side.data.datasource.RunningRemoteDataSource
import com.side.data.datasource.RunningRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataSourceModule {

    @Binds
    @Singleton
    fun provideRunningDataSource(impl: RunningRemoteDataSourceImpl): RunningRemoteDataSource

}