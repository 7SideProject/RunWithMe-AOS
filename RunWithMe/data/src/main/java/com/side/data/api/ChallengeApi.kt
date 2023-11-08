package com.side.data.api

import com.side.data.model.response.ChallengeListResponse
import com.side.data.model.response.ChallengeResponse
import com.side.domain.base.BaseResponse
import com.side.domain.model.Challenge
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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
    suspend fun isChallengeAlreadyJoin(@Part("challengeSeq") challengeSeq: Long): BaseResponse<String>

    @GET("challenge/my")
    suspend fun getMyChallengeList(@Query("cursorSeq") cursorSeq: Long, @Query("size") size: Int): BaseResponse<List<Challenge>>

    @GET("challenge/my/running")
    suspend fun getAvailableRunningList(@Query("cursorSeq") cursorSeq: Long, @Query("size") size: Int): BaseResponse<List<Challenge>>
}