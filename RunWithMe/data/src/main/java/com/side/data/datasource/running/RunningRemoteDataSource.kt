package com.side.data.datasource.running

import com.side.data.model.request.RunRecordRequest
import com.side.domain.base.BaseResponse
import kotlinx.coroutines.flow.Flow

interface RunningRemoteDataSource {

    fun postRunRecord(challengeSeq: Int, runRecordRequest: RunRecordRequest): Flow<BaseResponse<String>>

}