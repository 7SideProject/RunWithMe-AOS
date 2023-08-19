package com.side.data.datasource.token

import com.side.data.api.TokenApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenDataSourceImpl @Inject constructor(
    private val tokenApi: TokenApi
): TokenDataSource{

    override suspend fun refreshingToken(refreshToken: String) {
        tokenApi.refreshingToken(refreshToken)
    }

}