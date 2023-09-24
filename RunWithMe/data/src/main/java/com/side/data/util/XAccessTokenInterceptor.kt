package com.side.data.util

import android.util.Log
import com.google.gson.GsonBuilder
import com.side.data.BuildConfig
import com.side.data.api.TokenApi
import com.side.data.datasource.datastore.DataStoreDataSource
import com.side.domain.exception.BearerException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class XAccessTokenInterceptor @Inject constructor(
    private val dataStoreDataSource: DataStoreDataSource,

    ) : Interceptor {

//    private val tokenApi: TokenApi

//    init {
//        val gson = GsonBuilder().setLenient().create()
//        val client = OkHttpClient.Builder().build()
//        val BASE_URL = BuildConfig.BASEURL
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .client(client)
//            .build()
//        tokenApi = retrofit.create(TokenApi::class.java)
//    }

    override fun intercept(chain: Interceptor.Chain): Response = synchronized(chain){



        val token = runBlocking {
            dataStoreDataSource.getJWT().first()
        }

        Log.d("test123", "intercept TOKEN: $token ")
        val request = chain.request()
            .newBuilder()
            .addHeader("authorization", token)
            .build()
        val response = chain.proceed(request)
        Log.d("test123", "intercept RESPONSE : $response ")
        when (response.code) {
//            400 -> {
//
//            }
            401 -> {
                // 토큰 만료
                // jwt 토큰 갱신 요청 api
                // 토큰 저장
//                runBlocking {
//                    tokenApi.refreshingToken(dataStoreDataSource.getRefreshToken().first())
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
}

