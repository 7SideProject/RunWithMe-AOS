package com.side.domain.repository

import com.side.domain.base.BaseResponse
import com.side.domain.model.AllRunRecord
import com.side.domain.model.RunRecord
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

typealias PostRunRecordResponse = ResultType<BaseResponse<String>>
typealias NullResponse = ResultType<BaseResponse<Any?>>

interface RunningRepository {

    fun postRunRecord(challengeSeq: Long,
                      runRecord: RunRecord,
                      image: MultipartBody.Part): Flow<NullResponse>

    fun isAvailableRunningToday(challengeSeq: Long): Flow<NullResponse>
}