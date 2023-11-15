package com.side.data.datasource.token

import com.side.data.api.TokenApi
import com.side.domain.base.BaseResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenDataSourceImpl @Inject constructor(
    private val tokenApi: TokenApi
): TokenDataSource{

    override suspend fun refreshingToken(refreshToken: String): Response<BaseResponse<Any?>?> =
        tokenApi.refreshingToken("refresh_token", refreshToken)


}