package com.side.data.api

import com.side.data.model.request.RunRecordRequest
import com.side.domain.base.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface RunningApi {

    @Multipart
    @POST("challenge/{challengeSeq}/record")
    suspend fun postRunRecord(
        @Path("challengeSeq") challengeSeq: Long,
        @Part("runRecordPostDto") runRecordPostDto: RequestBody,
        @Part image: MultipartBody.Part
    ): BaseResponse<Any?>

}