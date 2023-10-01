package com.side.runwithme.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.side.data.api.UserApi
import com.side.runwithme.util.BASE_URL
import com.side.runwithme.util.TestXAccessTokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RemoteDataModule::class]
)
object TestRemoteDataModule {

    // HttpLoggingInterceptor DI
    @Provides
    @Singleton
    fun provideInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    // Gson DI
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().setLenient().create()
    }

    //OkHttpClient DI
    @Provides
    @Singleton
    fun provideOkHttpClient(
        testxAccessTokenInterceptor: TestXAccessTokenInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(testxAccessTokenInterceptor)
            .build()
    }

    //Retrofit DI
    @Provides
    @Singleton
    fun provideRetrofitInstance(gson: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }


    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }
}