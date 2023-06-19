package com.side.data.util

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.side.data.datasource.datastore.DataStoreDataSource
import com.side.data.util.preferencesKeys.JWT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class XAccessTokenInterceptor @Inject constructor(
    private val dataStoreDataSource: DataStoreDataSource
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val token = runBlocking {
            dataStoreDataSource.getJWT().first()
        }

        val request = chain.request()
            .newBuilder()
            .addHeader("JWT", token)
            .build()
        val response = chain.proceed(request)

        when (response.code) {
            400 -> {

            }
            401 -> {
                // 토큰 만료
                // jwt 토큰 갱신 요청 api
                // 토큰 저장
//                getToken(response)
                // request 다시 보내기

                // refresh 토큰도 만료되면 로그인화면 보내기
            }
            403 -> {

            }
            404 -> {

            }
            200 -> {
                getToken(response)

                return response
            }
        }


        return response
    }

    private fun getToken(response: Response){
        val allHeaders = response.headers
        val jwt = allHeaders.get("authorization") ?: ""
        val refreshToken = allHeaders.get("set-cookie")?.let {
            it.split("; ").get(0).split("=").get(1)
        }

        Log.d("test123", "intercept: headers : ${allHeaders}")
        Log.d("test123", "intercept: jwt : ${jwt}")
        Log.d("test123", "intercept: refreshtoken : ${refreshToken}")

        CoroutineScope(Dispatchers.IO).launch {
            saveToken(jwt, refreshToken ?: "")
        }
    }

    private suspend fun saveToken(jwt: String, refreshToken: String) {
        dataStoreDataSource.saveToken(jwt, refreshToken)
    }
}