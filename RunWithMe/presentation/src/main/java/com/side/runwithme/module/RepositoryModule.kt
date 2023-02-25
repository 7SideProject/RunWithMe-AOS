package com.side.runwithme.module

import com.side.data.repository.UserRepositoryImpl
import com.side.domain.repository.UserRepository
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
    abstract fun provideUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository


}