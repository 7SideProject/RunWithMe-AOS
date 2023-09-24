package com.side.data.api

import com.side.data.model.response.ChallengeListResponse
import com.side.data.model.response.ChallengeResponse
import com.side.domain.base.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ChallengeApi {

    @GET("challenge/all")
    suspend fun getChallengeList(
        @Query("cursorSeq") cursorSeq: Int,
        @Query("size") size: Int,
    ): BaseResponse<ChallengeListResponse>


}