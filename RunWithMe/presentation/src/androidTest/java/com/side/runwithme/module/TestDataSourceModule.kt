package com.side.runwithme.module

import com.side.data.datasource.user.UserRemoteDataSource
import com.side.data.datasource.user.UserRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton


@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataSourceModule::class]
)
interface TestDataSourceModule {

    @Binds
    @Singleton
    fun provideUserDataSource(
        impl: UserRemoteDataSourceImpl
    ): UserRemoteDataSource

}