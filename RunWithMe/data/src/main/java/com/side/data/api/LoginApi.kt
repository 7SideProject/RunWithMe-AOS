package com.side.data.api

import com.side.data.model.request.EmailLoginRequest
import com.side.data.model.request.KakaoLoginRequest
import com.side.data.model.response.EmailLoginResponse
import com.side.data.model.response.KaKaoLoginResponse
import com.side.domain.base.BaseResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("users/login")
    suspend fun loginWithEmail(@Body request: EmailLoginRequest): BaseResponse<EmailLoginResponse?>

    @POST("users/kakao/login")
    suspend fun loginWithKakao(@Body request: KakaoLoginRequest): BaseResponse<KaKaoLoginResponse>
}