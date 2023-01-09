package com.side.data.api

import com.side.data.model.request.LoginRequest
import com.side.data.model.response.UserResponse
import com.side.domain.base.BaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {
    @POST
    suspend fun loginUser(@Body loginRequest: LoginRequest): BaseResponse<UserResponse>
}