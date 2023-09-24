package com.side.data.datasource.token

interface TokenDataSource {

    suspend fun refreshingToken(refreshToken: String)

}