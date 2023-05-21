package com.side.runwithme.util

import android.content.SharedPreferences
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.side.runwithme.util.preferencesKeys.JWT
import decrypt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

private const val TAG = "XAccessTokenInterceptor"

class XAccessTokenRequestInterceptor @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        var token = ""
        CoroutineScope(Dispatchers.Main).launch {
            token = dataStore.getValue(JWT, KEY_STRING).first().toString()
        }

        val request = chain.request().newBuilder()
            .addHeader("JWT", "Bearer ${decrypt(token)}")
            .build()


        return chain.proceed(request)
    }


}