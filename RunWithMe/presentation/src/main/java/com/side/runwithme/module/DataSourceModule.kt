package com.side.runwithme.module

import com.side.data.datasource.RunningRemoteDataSource
import com.side.data.datasource.RunningRemoteDataSourceImpl
import com.side.data.datasource.datastore.DataStoreDataSource
import com.side.data.datasource.datastore.DataStoreDataSourceImpl
import com.side.data.datasource.user.UserRemoteDataSource
import com.side.data.datasource.user.UserRemoteDataSourceImpl

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

    @Binds
    @Singleton
    fun provideUserDataSource(
        impl: UserRemoteDataSourceImpl
    ): UserRemoteDataSource

    @Binds
    @Singleton
    fun provideDataStoreDataSource(
        impl: DataStoreDataSourceImpl
    ): DataStoreDataSource

}