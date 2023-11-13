package com.side.data.api

import com.side.data.model.response.ChallengeListResponse
import com.side.data.model.response.ChallengeResponse
import com.side.domain.base.BaseResponse
import com.side.domain.model.Challenge
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ChallengeApi {

    @GET("challenge/all")
    suspend fun getRecruitingChallengeList(
        @Query("cursorSeq") cursorSeq: Long,
        @Query("size") size: Int,
    ): BaseResponse<List<Challenge>>


    @Multipart
    @POST("challenge")
    suspend fun createChallenge(@Part("challengeCreateDto") challengeCreateDto: RequestBody, @Part image: MultipartBody.Part?): BaseResponse<Any?>

    @GET("challenge/{challengeSeq}/is")
    suspend fun isChallengeAlreadyJoin(@Path("challengeSeq") challengeSeq: Long): BaseResponse<String>

    @GET("challenge/my")
    suspend fun getMyChallengeList(@Query("cursorSeq") cursorSeq: Long, @Query("size") size: Int): BaseResponse<List<Challenge>>

    @POST("challenge/{challengeSeq}/join")
    suspend fun joinChallenge(@Path("challengeSeq") challengeSeq: Long, @Query("password") password: String?): BaseResponse<String?>

    @DELETE("challenge/{challengeSeq}")
    suspend fun leaveChallenge(@Path("challengeSeq") challengeSeq: Long): BaseResponse<Any?>
}