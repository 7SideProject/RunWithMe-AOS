package com.side.runwithme.module

import com.side.data.repository.UserRepositoryImpl
import com.side.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
interface TestRepositoryModule {

    @Binds
    @Singleton
    fun provideTestUserRepository(impl: UserRepositoryImpl): UserRepository



}