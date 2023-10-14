package com.side.data.api

import com.side.data.model.request.EmailLoginRequest
import com.side.data.model.request.JoinRequest
import com.side.data.model.response.EmailLoginResponse
import com.side.data.model.response.JoinResponse
import com.side.data.model.response.UserResponse
import com.side.domain.base.BaseResponse
import com.side.domain.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {
    @GET("login/oauth2/code/kakao")
    suspend fun login(
        @Query("code") code: String,
        @Query("state") state: String
    ): BaseResponse<User>

    @POST("users/join")
    suspend fun join(@Body request: JoinRequest): BaseResponse<UserResponse>

//    @POST("users/login")
//    suspend fun loginWithEmail(@Body request: EmailLoginRequest): BaseResponse<EmailLoginResponse?>

//    @POST("/token")
//    suspend fun refreshingToken(@Body refreshToken: String)

    @GET("users/{userSeq}")
    suspend fun getUserProfile(@Query("userSeq") userSeq: Long): BaseResponse<UserResponse>
}