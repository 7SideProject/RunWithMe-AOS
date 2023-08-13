package com.side.data.api

import retrofit2.http.Body
import retrofit2.http.POST

interface TokenApi {

    @POST("/token")
    suspend fun refreshingToken(@Body refreshToken: String)

}