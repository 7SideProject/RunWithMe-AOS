package com.side.runwithme.util

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.side.runwithme.util.preferencesKeys.JWT
import encrypt
import kotlinx.coroutines.*
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class XAccessTokenResponseInterceptor @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        val response = chain.proceed(request)

        val allHeaders = response.headers
        val value = allHeaders["authorization"]
        Log.d("test123", "intercept: $allHeaders")
        Log.d("test123", "auth: $value")

        CoroutineScope(Dispatchers.IO).launch {
            dataStore.saveValue(JWT, encrypt(value!!))

        }

        return response
    }


}