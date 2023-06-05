package com.side.runwithme.module

import com.google.gson.Gson
import com.side.data.api.UserApi
import com.side.runwithme.util.BASE_URL
import com.side.runwithme.util.TestXAccessTokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestRemoteDataModule {

    //OkHttpClient DI
    @Provides
    @Singleton
    @Named("testClient")
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
    @Named("testRetrofit")
    fun provideRetrofitInstance(gson: Gson, @Named("testClient") client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }


    @Provides
    @Singleton
    @Named("testUserApi")
    fun provideUserApi(@Named("testRetrofit") retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }
}