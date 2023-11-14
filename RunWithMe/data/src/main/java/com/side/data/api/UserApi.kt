package com.side.data.api

import com.side.data.model.request.EditProfileRequest
import com.side.data.model.request.FindPasswordRequest
import com.side.data.model.request.JoinRequest
import com.side.data.model.response.DailyCheckResponse
import com.side.data.model.response.DuplicateCheckResponse
import com.side.data.model.response.TotalRecordResponse
import com.side.data.model.response.UserResponse
import com.side.domain.base.BaseResponse
import com.side.domain.model.User
import com.side.domain.repository.NullResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
    @GET("login/oauth2/code/kakao")
    suspend fun login(
        @Query("code") code: String,
        @Query("state") state: String
    ): BaseResponse<User>

    @POST("users/join")
    suspend fun join(@Body request: JoinRequest): BaseResponse<UserResponse>

    @GET("users/{userSeq}")
    suspend fun getUserProfile(@Path("userSeq") userSeq: Long): BaseResponse<UserResponse>

    @GET("users/duplicate-email")
    suspend fun checkIdIsDuplicate(@Query("email") email: String): BaseResponse<DuplicateCheckResponse>

    @GET("users/duplicate-nickname")
    suspend fun checkNicknameIsDuplicate(@Query("nickname") nickname: String): BaseResponse<DuplicateCheckResponse>

    @PUT("users/{userEmail}/password")
    suspend fun changePassword(@Path("userEmail") email: String, @Body passwordRequest: FindPasswordRequest): BaseResponse<Any?>
    @POST("users/{userSeq}/connect")
    suspend fun dailyCheck(@Path("userSeq") userSeq: Long): BaseResponse<DailyCheckResponse>

    @GET("users/{userSeq}/total-record")
    suspend fun getTotalRecord(@Path("userSeq") userSeq: Long): BaseResponse<TotalRecordResponse>

    @PUT("/users/{userSeq}/profile")
    suspend fun editProfile(@Path("userSeq") userSeq: Long, @Body editProfileRequest: EditProfileRequest): BaseResponse<UserResponse>

    @DELETE("users/{userSeq}")
    suspend fun deleteUser(@Path("userSeq") userSeq: Long): BaseResponse<Any?>
}