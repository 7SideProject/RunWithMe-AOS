package com.side.data.api

import com.side.domain.base.BaseResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TokenApi {

    @FormUrlEncoded
    @POST("token")
    suspend fun refreshingToken(@Field("grantType") grantType: String, @Field("refreshToken") refreshToken: String): Response<BaseResponse<Any?>?>

}