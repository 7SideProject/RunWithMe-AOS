package com.side.data.datasource.token

import com.side.data.model.response.TokenResponse
import com.side.domain.base.BaseResponse
import retrofit2.Response


interface TokenDataSource {

    suspend fun refreshingToken(refreshToken: String): Response<Any?>

}