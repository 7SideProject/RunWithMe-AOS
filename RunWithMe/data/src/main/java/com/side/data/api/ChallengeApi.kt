package com.side.data.api

import com.side.data.model.response.ChallengeResponse
import com.side.domain.base.BaseResponse
import retrofit2.http.GET

interface ChallengeApi {

    @GET("challenge")
    suspend fun getChallengeList(): BaseResponse<List<ChallengeResponse>>


}