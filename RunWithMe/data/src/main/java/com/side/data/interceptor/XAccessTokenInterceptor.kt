package com.side.data.interceptor

import android.util.Log
import com.side.data.datasource.datastore.DataStoreDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class XAccessTokenInterceptor @Inject constructor(
    private val dataStoreDataSource: DataStoreDataSource
    ) : Interceptor {

    companion object {
        private const val AUTHORIZATION = "authorization"
    }

    override fun intercept(chain: Interceptor.Chain): Response = synchronized(chain) {

        val token = runBlocking {
            dataStoreDataSource.getJWT().first()
        }

        val request = chain.request()
            .newBuilder()
            .addHeader(AUTHORIZATION, token)
            .build()
        val response = chain.proceed(request)

        return response
    }

}

