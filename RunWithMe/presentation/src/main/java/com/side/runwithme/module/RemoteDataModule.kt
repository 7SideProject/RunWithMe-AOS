package com.side.runwithme.module


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.side.data.api.ChallengeApi
import com.side.data.api.LoginApi
import com.side.data.api.RunningApi
import com.side.data.api.TokenApi
import com.side.data.api.UserApi
import com.side.data.util.XAccessTokenInterceptor
import com.side.runwithme.util.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteDataModule {

//    @Qualifier
//    @Retention(AnnotationRetention.RUNTIME)
//    annotation class LoginHeaderOkhttp

//    @Qualifier
//    @Retention(AnnotationRetention.RUNTIME)
//    annotation class BearerHeaderOkhttp
//
////    @Qualifier
////    @Retention(AnnotationRetention.RUNTIME)
////    annotation class LoginHeaderRetrofit
//
//    @Qualifier
//    @Retention(AnnotationRetention.RUNTIME)
//    annotation class BearerHeaderRetrofit

    // HttpLoggingInterceptor DI
    @Provides
    @Singleton
    fun provideInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    //OkHttpClient DI
//    @Provides
//    @Singleton
//    @LoginHeaderOkhttp
//    fun provideLoginHeaderOkHttpClient(
//        loginInterceptor: LoginInterceptor
//    ): OkHttpClient {
//        return OkHttpClient.Builder()
//            .addNetworkInterceptor(loginInterceptor)
//            .build()
//    }

    @Provides
    @Singleton
    fun provideBearerHeaderOkHttpClient(
        xAccessTokenInterceptor: XAccessTokenInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(xAccessTokenInterceptor)
            .build()
    }

    // Gson DI
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().setLenient().create()
    }

//    //Retrofit DI
//    @Provides
//    @Singleton
//    @LoginHeaderRetrofit
//    fun provideLoginHeaderRetrofitInstance(@LoginHeaderOkhttp client: OkHttpClient, gson: Gson): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .client(client)
//            .build()
//    }

    //Retrofit DI
    @Provides
    @Singleton
    fun provideBearerHeaderRetrofitInstance( client: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

//    @Provides
//    @Singleton
//    fun provideTokenApi(retrofit: Retrofit): TokenApi {
//        return retrofit.create(TokenApi::class.java)
//    }

    @Provides
    @Singleton
    fun provideLoginApi( retrofit: Retrofit): LoginApi {
        return retrofit.create(LoginApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRunningApi( retrofit: Retrofit): RunningApi {
        return retrofit.create(RunningApi::class.java)
    }

    @Provides
    @Singleton
    fun provideChallengeApi(retrofit: Retrofit): ChallengeApi {
        return retrofit.create(ChallengeApi::class.java)
    }


}