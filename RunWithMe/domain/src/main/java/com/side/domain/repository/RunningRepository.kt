package com.side.domain.repository

import com.side.domain.base.BaseResponse
import com.side.domain.model.AllRunRecord
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow


interface RunningRepository {

    fun postRunRecord(challengeSeq: Int, allRunRecord: AllRunRecord): Flow<ResultType<BaseResponse<String>>>

}