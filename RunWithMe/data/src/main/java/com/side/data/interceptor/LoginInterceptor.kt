package com.side.data.interceptor

import com.side.data.datasource.datastore.DataStoreDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class LoginInterceptor @Inject constructor(
    private val dataStoreDataSource: DataStoreDataSource
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response =
        synchronized(chain){
            val request = chain.request()
                .newBuilder()
                .build()

            val response = chain.proceed(request)

            when(response.code){
                200 -> {
                    getToken(response)
                }
                else -> {

                }
            }

            return response
        }


    private fun getToken(response: Response){
        val allHeaders = response.headers
        val jwt = allHeaders.get("authorization") ?: ""
        val refreshToken = allHeaders.get("set-cookie")?.let {
            it.split("; ").get(0).split("=").get(1)
        } ?: ""

        runBlocking {
            saveToken(jwt, refreshToken)
        }
    }

    private suspend fun saveToken(jwt: String, refreshToken: String) {
        dataStoreDataSource.saveToken(jwt, refreshToken)
    }

}