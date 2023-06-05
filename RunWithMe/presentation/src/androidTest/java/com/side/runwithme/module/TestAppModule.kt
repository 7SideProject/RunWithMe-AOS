package com.side.runwithme.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.side.data.repository.UserRepositoryImpl
import com.side.domain.repository.UserRepository
import com.side.domain.usecase.user.LoginUseCase
import com.side.runwithme.util.TEST_DATASTORE_NAME
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.test.TestScope
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TestAppModule {

    @Singleton
    @Provides
    @Named("testDatastore")
    fun provideDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = TestScope(),
            produceFile = { appContext.preferencesDataStoreFile(TEST_DATASTORE_NAME) })

}