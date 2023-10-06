package com.side.data.repository

import com.side.data.datasource.running.RunningRemoteDataSource
import com.side.data.mapper.mapperToRunRecordRequest
import com.side.data.util.asResult
import com.side.data.util.emitResultTypeError
import com.side.data.util.emitResultTypeLoading
import com.side.data.util.emitResultTypeSuccess
import com.side.domain.model.AllRunRecord
import com.side.domain.repository.PostRunRecordResponse
import com.side.domain.repository.RunningRepository
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RunningRepositoryImpl @Inject constructor(
    private val runningRemoteDataSource: RunningRemoteDataSource
): RunningRepository{

    override fun postRunRecord(
        challengeSeq: Int,
        allRunRecord: AllRunRecord
    ): Flow<PostRunRecordResponse> = runningRemoteDataSource.postRunRecord(challengeSeq, allRunRecord.mapperToRunRecordRequest()).asResult {
        /** 성공, 실패 나누기 **/
        ResultType.Success(it)
    }



}