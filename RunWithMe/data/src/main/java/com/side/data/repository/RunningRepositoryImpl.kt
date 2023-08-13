package com.side.data.repository

import com.side.data.datasource.running.RunningRemoteDataSourceImpl
import com.side.data.mapper.mapperToRunRecordRequest
import com.side.data.util.emitResultTypeError
import com.side.data.util.emitResultTypeFail
import com.side.data.util.emitResultTypeLoading
import com.side.data.util.emitResultTypeSuccess
import com.side.domain.base.BaseResponse
import com.side.domain.model.AllRunRecord
import com.side.domain.repository.PostRunRecordResponse
import com.side.domain.repository.RunningRepository
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RunningRepositoryImpl @Inject constructor(
    private val runningRemoteDataSourceImpl: RunningRemoteDataSourceImpl
): RunningRepository{

    override fun postRunRecord(
        challengeSeq: Int,
        allRunRecord: AllRunRecord
    ): Flow<PostRunRecordResponse> = flow {
        emitResultTypeLoading()
        runningRemoteDataSourceImpl.postRunRecord(challengeSeq, allRunRecord.mapperToRunRecordRequest()).collect {
            /** 성공, 실패 나누기 **/
            emitResultTypeSuccess(it)
//            emitResultTypeFail(it)
        }
    }.catch {
        emitResultTypeError(it)
    }

}