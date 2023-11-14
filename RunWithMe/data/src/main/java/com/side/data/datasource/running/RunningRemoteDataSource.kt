package com.side.data.datasource.running

import com.side.data.model.request.RunRecordRequest
import com.side.domain.base.BaseResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface RunningRemoteDataSource {

    fun postRunRecord(challengeSeq: Long, runRecordRequest: RequestBody, image: MultipartBody.Part): Flow<BaseResponse<Any?>>

}