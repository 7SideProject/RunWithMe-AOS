package com.side.data.api

import com.side.data.model.request.RunRecordRequest
import com.side.domain.base.BaseResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface RunningApi {

    @POST("/challenge/{challengeSeq}/record")
    suspend fun postRunRecord(@Path("challengeSeq") challengeSeq: Int, @Body runRecordRequest: RunRecordRequest): BaseResponse<String>

}