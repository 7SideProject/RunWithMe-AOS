package com.side.runwithme.module

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.side.data.api.UserApi
import com.side.data.datasource.UserRemoteDataSource
import com.side.data.repository.UserRepositoryImpl
import com.side.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestRepositoryModule {

    @Provides
    @Singleton
    @Named("testUserRepo")
    fun provideTestUserRepository(@Named("testUserApi") userApi: UserApi, @Named("testDatastore") dataStore: DataStore<Preferences>): UserRepository {
        return UserRepositoryImpl(
            dataStore = dataStore,
            userRemoteDataSource = UserRemoteDataSource(userApi)
        )

    }

}