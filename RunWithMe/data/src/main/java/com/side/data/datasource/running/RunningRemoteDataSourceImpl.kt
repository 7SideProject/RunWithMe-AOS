package com.side.data.datasource.running

import com.side.data.api.RunningApi
import com.side.data.model.request.RunRecordRequest
import com.side.domain.base.BaseResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RunningRemoteDataSourceImpl @Inject constructor(
    private val runningApi: RunningApi
): RunningRemoteDataSource {

    override fun postRunRecord(challengeSeq: Long, runRecordRequest: RequestBody, image: MultipartBody.Part): Flow<BaseResponse<Any?>> = flow {
        emit(runningApi.postRunRecord(challengeSeq, runRecordRequest, image))
    }

}