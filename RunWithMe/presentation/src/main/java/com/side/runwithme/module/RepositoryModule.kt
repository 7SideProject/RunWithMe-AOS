package com.side.runwithme.module

import com.side.data.datasource.UserRemoteDataSource
import com.side.data.repository.UserRepositoryImpl
import com.side.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        userRemoteDataSource: UserRemoteDataSource
    ): UserRepository{
        return UserRepositoryImpl(userRemoteDataSource)
    }
}