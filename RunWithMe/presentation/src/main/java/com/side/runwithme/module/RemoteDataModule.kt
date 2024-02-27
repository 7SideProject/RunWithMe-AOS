package com.side.runwithme.module


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.side.data.api.ChallengeApi
import com.side.data.api.LoginApi
import com.side.data.api.RunningApi
import com.side.data.api.SocialJoinApi
import com.side.data.api.TokenApi
import com.side.data.api.UserApi
import com.side.data.interceptor.AccessTokenAuthenticator
import com.side.data.interceptor.LoginInterceptor
import com.side.data.interceptor.SocialJoinTokenInterceptor
import com.side.data.interceptor.XAccessTokenInterceptor
import com.side.runwithme.util.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteDataModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class LoginHeaderOkhttp

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class SocialJoinHeaderOkhttp

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class BearerHeaderOkhttp

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class NoHeaderOkhttp

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class LoginHeaderRetrofit

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class SocialJoinHeaderRetrofit

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class BearerHeaderRetrofit

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class NoHeaderRetrofit

    // HttpLoggingInterceptor DI
    @Provides
    @Singleton
    fun provideInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

//    OkHttpClient DI
    @Provides
    @Singleton
    @LoginHeaderOkhttp
    fun provideLoginHeaderOkHttpClient(
        loginInterceptor: LoginInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(loginInterceptor)
            .build()
    }

    @Provides
    @SocialJoinHeaderOkhttp
    @Singleton
    fun provideSocialJoinHeaderOkHttpClient(
        socialJoinTokenInterceptor: SocialJoinTokenInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(socialJoinTokenInterceptor)
            .build()
    }

    @Provides
    @BearerHeaderOkhttp
    @Singleton
    fun provideBearerHeaderOkHttpClient(
        xAccessTokenInterceptor: XAccessTokenInterceptor,
        accessTokenAuthenticator: AccessTokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(xAccessTokenInterceptor)
            .authenticator(accessTokenAuthenticator)
            .build()
    }

    @Provides
    @NoHeaderOkhttp
    @Singleton
    fun provideNoHeaderOkHttpClient(
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    // Gson DI
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().setLenient().create()
    }

    //Retrofit DI
    @Provides
    @Singleton
    @LoginHeaderRetrofit
    fun provideLoginHeaderRetrofitInstance(@LoginHeaderOkhttp client: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    //Retrofit DI
    @Provides
    @Singleton
    @SocialJoinHeaderRetrofit
    fun provideSocialJoinHeaderRetrofitInstance(@SocialJoinHeaderOkhttp client: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    //Retrofit DI
    @Provides
    @Singleton
    @BearerHeaderRetrofit
    fun provideBearerHeaderRetrofitInstance(@BearerHeaderOkhttp client: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    //Retrofit DI
    @Provides
    @Singleton
    @NoHeaderRetrofit
    fun provideNoHeaderRetrofitInstance(@NoHeaderOkhttp client: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideTokenApi(@NoHeaderRetrofit retrofit: Retrofit): TokenApi {
        return retrofit.create(TokenApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLoginApi(@LoginHeaderRetrofit retrofit: Retrofit): LoginApi {
        return retrofit.create(LoginApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(@BearerHeaderRetrofit retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRunningApi(@BearerHeaderRetrofit retrofit: Retrofit): RunningApi {
        return retrofit.create(RunningApi::class.java)
    }

    @Provides
    @Singleton
    fun provideChallengeApi(@BearerHeaderRetrofit retrofit: Retrofit): ChallengeApi {
        return retrofit.create(ChallengeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSocialJoinApi(@SocialJoinHeaderRetrofit retrofit: Retrofit): SocialJoinApi {
        return retrofit.create(SocialJoinApi::class.java)
    }

}