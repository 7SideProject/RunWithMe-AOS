package com.side.data.datasource

import com.side.data.api.RunningApi
import com.side.data.model.request.RunRecordRequest
import com.side.domain.base.BaseResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RunningRemoteDataSource @Inject constructor(
    private val runningApi: RunningApi
){

    fun postRunRecord(challengeSeq: Int, runRecordRequest: RunRecordRequest): Flow<BaseResponse<String>> = flow {
        emit(runningApi.postRunRecord(challengeSeq, runRecordRequest))
    }

}