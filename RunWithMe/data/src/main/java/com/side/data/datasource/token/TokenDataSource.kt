package com.side.data.datasource.token

import retrofit2.Response


interface TokenDataSource {

    suspend fun refreshingToken(refreshToken: String): Response<Any?>

}