package com.side.data.api

import com.side.data.model.request.LoginRequest
import com.side.data.model.response.UserResponse
import com.side.domain.base.BaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
    @POST("login/oauth2/code/kakao")
    suspend fun login(@Query("code") code: String, @Query("state") state: String): BaseResponse<String>

}