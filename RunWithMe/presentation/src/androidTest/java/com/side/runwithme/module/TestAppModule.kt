package com.side.runwithme.module

import android.content.Context
import com.side.data.repository.UserRepositoryImpl
import com.side.domain.repository.UserRepository
import com.side.domain.usecase.user.LoginUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TestAppModule {

    @Binds
    @Named("test_repo")
    abstract fun provideUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

}