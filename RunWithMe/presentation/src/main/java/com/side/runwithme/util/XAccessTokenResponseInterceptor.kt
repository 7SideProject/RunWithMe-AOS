package com.side.runwithme.util

import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class XAccessTokenResponseInterceptor @Inject constructor(
    private val sharedPref: SharedPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        val response = chain.proceed(request)

        val allHeaders = response.headers
        val value = allHeaders["authorization"]
        Log.d("test123", "intercept: $allHeaders")
        Log.d("test123", "auth: $value")

        return response
    }


}