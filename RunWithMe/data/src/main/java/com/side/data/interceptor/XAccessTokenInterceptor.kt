package com.side.data.interceptor

import android.util.Log
import com.side.data.datasource.datastore.DataStoreDataSource
import com.side.data.datasource.token.TokenDataSource
import com.side.domain.exception.BearerException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class XAccessTokenInterceptor @Inject constructor(
    private val dataStoreDataSource: DataStoreDataSource,
    private val tokenDataSource: TokenDataSource
    ) : Interceptor {



    override fun intercept(chain: Interceptor.Chain): Response = synchronized(chain) {


        val token = runBlocking {
            dataStoreDataSource.getJWT().first()
        }

        val request = chain.request()
            .newBuilder()
            .addHeader("authorization", token)
            .build()
        val response = chain.proceed(request)

        when (response.code) {
//            400 -> {
//
//            }
            401 -> {
                // 토큰 만료
                // jwt 토큰 갱신 요청 api
                // 토큰 저장
                runBlocking {
                    dataStoreDataSource.saveToken("", "")
                }

                getResponseBody(response)

//                runBlocking {
//                    tokenDataSource.refreshingToken(dataStoreDataSource.getRefreshToken().first())
//                    if(response.isSuccessful){
//                        getToken(response)
//                    }else {
//                        // refresh 토큰도 만료되면 로그인화면 보내기
//                        throw Exception()
//                    }
//                }

//                getToken(response)
                // request 다시 보내기


            }
//            403 -> {
//
//            }
//            404 -> {
//
//            }
            200 -> {


            }
        }




        return response
    }

    private fun getToken(response: Response) {
        val allHeaders = response.headers
        val jwt = allHeaders.get("authorization") ?: ""
        val refreshToken = allHeaders.get("set-cookie")?.let {
            it.split("; ").get(0).split("=").get(1)
        } ?: ""

        Log.d("test123", "intercept: headers : ${allHeaders}")
        Log.d("test123", "intercept: jwt : ${jwt}")
        Log.d("test123", "intercept: refreshtoken : ${refreshToken}")

        runBlocking {
            saveToken(jwt, refreshToken)
        }
    }

    private suspend fun saveToken(jwt: String, refreshToken: String) {
        dataStoreDataSource.saveToken(jwt, refreshToken)
    }

    private fun getResponseBody(response: Response){
        val responseJson = response.extractResponseJson()
        val baseResponseCode = if(responseJson.has("code")) responseJson.get("code").toString().toInt() else 0

        isTokenError(baseResponseCode)
    }

    private fun Response.extractResponseJson(): JSONObject {
        val jsonString: String = this.body?.string() ?: "{}"
        return try {
            JSONObject(jsonString)
        } catch(exception: Exception) {
            Log.e("UnboxingInterceptor", "서버 응답이 json이 아님: $jsonString")
            throw exception
        }
    }

    private fun isTokenError(code: Int) {
        if (code == TokenError.NOTHEADERTOKEN.code || code == TokenError.INVALIDSIGNATURE.code || code == TokenError.INVALIDTOKEN.code || code == TokenError.NOTSUPPORTEDTOKEN.code
//            || code == TokenError.EXPIREDTOKEN.code
        ) {

            throw BearerException()
        }
    }

    enum class TokenError(val code: Int) {
        NOTHEADERTOKEN(-601), INVALIDSIGNATURE(-602), INVALIDTOKEN(-603), EXPIREDTOKEN(-604), NOTSUPPORTEDTOKEN(
            -605
        )
    }
}

