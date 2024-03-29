package com.side.data.api

import com.side.data.model.response.ChallengeBoardsResponse
import com.side.data.model.response.ChallengeDetailResponse
import com.side.data.model.response.ChallengeListResponse
import com.side.data.model.response.ChallengeRecordsResponse
import com.side.data.model.response.CreateBoardResponse
import com.side.data.model.response.MyTotalRecordInChallengeResponse
import com.side.domain.base.BaseResponse
import com.side.domain.model.Challenge
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ChallengeApi {

    @GET("challenge/all/recruit")
    suspend fun getRecruitingChallengeList(
        @Query("cursorSeq") cursorSeq: Long,
        @Query("dateStart") dateStart: String,
        @Query("size") size: Int,
    ): BaseResponse<List<ChallengeListResponse>>


    @Multipart
    @POST("challenge")
    suspend fun createChallenge(@Part("challengeCreateDto") challengeCreateDto: RequestBody, @Part image: MultipartBody.Part?): BaseResponse<Any?>

    @GET("challenge/{challengeSeq}/is")
    suspend fun isChallengeAlreadyJoin(@Path("challengeSeq") challengeSeq: Long): BaseResponse<String>

    @GET("challenge/my")
    suspend fun getMyChallengeList(@Query("cursorSeq") cursorSeq: Long, @Query("size") size: Int): BaseResponse<List<ChallengeListResponse>>

    @GET("challenge/my/running")
    suspend fun getAvailableRunningList(@Query("cursorSeq") cursorSeq: Long, @Query("size") size: Int): BaseResponse<List<ChallengeListResponse>>

    @POST("challenge/{challengeSeq}/join")
    suspend fun joinChallenge(@Path("challengeSeq") challengeSeq: Long, @Query("password") password: String?): BaseResponse<String?>

    @DELETE("challenge/{challengeSeq}")
    suspend fun leaveChallenge(@Path("challengeSeq") challengeSeq: Long): BaseResponse<Any?>

    @GET("challenge/{challengeSeq}")
    suspend fun getChallengeDetail(@Path("challengeSeq") challengeSeq: Long): BaseResponse<ChallengeDetailResponse>

    @GET("challenge/{challengeSeq}/all")
    suspend fun getRecordsList(@Path("challengeSeq") challengeSeq: Long, @Query("size") size: Int): BaseResponse<List<ChallengeRecordsResponse>>

    @Multipart
    @POST("challenge/{challengeSeq}/board")
    suspend fun createBoard(@Path("challengeSeq") challengeSeq: Long, @Part("challengeBoardContent") challengeBoardContent: RequestBody, @Part image: MultipartBody.Part?): BaseResponse<CreateBoardResponse>

    @GET("challenge/{challengeSeq}/board")
    suspend fun getChallengeBoards(@Path("challengeSeq") challengeSeq: Long, @Query("cursorSeq") cursorSeq: Long, @Query("size") size: Int): BaseResponse<List<ChallengeBoardsResponse>>

    @DELETE("challenge/board/{boardSeq}")
    suspend fun deleteBoard(@Path("boardSeq") boardSeq: Long): BaseResponse<Any?>

    @POST("challenge/warn/{boardSeq}")
    suspend fun reportBoard(@Path("boardSeq") boardSeq: Long): BaseResponse<Any?>

    @GET("challenge/{challengeSeq}/record/my-total")
    suspend fun getMyTotalRecordInChallenge(@Path("challengeSeq") challengeSeq: Long): BaseResponse<MyTotalRecordInChallengeResponse>

}