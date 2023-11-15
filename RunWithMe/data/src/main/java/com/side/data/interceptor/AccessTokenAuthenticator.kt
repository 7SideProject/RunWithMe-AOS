package com.side.data.interceptor

import android.util.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.side.data.datasource.datastore.DataStoreDataSource
import com.side.data.datasource.token.TokenDataSource
import com.side.domain.base.BaseResponse
import com.side.domain.exception.BearerException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessTokenAuthenticator @Inject constructor(
    private val dataStoreDataSource: DataStoreDataSource,
    private val tokenDataSource: TokenDataSource
): Authenticator {

    companion object {
        private const val AUTHORIZATION = "authorization"
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        if(response.code == 401) {
            try {
                val refreshResponse = runBlocking {
                    val refreshToken = dataStoreDataSource.getRefreshToken().first()
                    tokenDataSource.refreshingToken(refreshToken)
                }

                Log.d("test123", "authenticate errorbody: ${(refreshResponse.errorBody()).toString()}")
                Log.d("test123", "authenticate body : ${refreshResponse.body()}")

                if (refreshResponse.isSuccessful) {
                    val newToken = getToken(refreshResponse)
                    if (newToken.isBlank()) {
                        throw BearerException()
                    }

                    val newRequest = response.request
                        .newBuilder()
                        .header(AUTHORIZATION, newToken)
                        .build()

                    return newRequest
                } else {

                    throw BearerException()
                }
            } catch (exception: Exception) {
                Firebase.crashlytics.recordException(exception)

                runBlocking {
                    saveToken("", "")
                }

                throw BearerException()
            }
        }

        return null
    }

    private fun getToken(response: retrofit2.Response<BaseResponse<Any?>?>): String {
        val allHeaders = response.headers()
        val jwt = allHeaders.get("authorization") ?: ""
        val refreshToken = allHeaders.get("set-cookie")?.let {
            it.split("; ").get(0).split("=").get(1)
        } ?: ""

        /** refresh token 갱신되는지 확인해야함 **/
        Log.d("test123", "authenticator : headers : ${allHeaders}")
        Log.d("test123", "authenticator: jwt : ${jwt}")
        Log.d("test123", "authenticator: refreshtoken : ${refreshToken}")

        runBlocking {
            saveToken(jwt, refreshToken)
        }

        return jwt
    }

    private suspend fun saveToken(jwt: String, refreshToken: String) {
        dataStoreDataSource.saveToken(jwt, refreshToken)
    }
}