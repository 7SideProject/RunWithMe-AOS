package com.side.data.api

import com.side.data.model.request.EditProfileRequest
import com.side.data.model.response.UserResponse
import com.side.domain.base.BaseResponse
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

interface SocialJoinApi {
    @PUT("/users/{userSeq}/profile")
    suspend fun joinSocialUser(@Path("userSeq") userSeq: Long, @Body editProfileRequest: EditProfileRequest): BaseResponse<UserResponse>
}