package com.side.data.repository

import com.side.data.datasource.RunningRemoteDataSourceImpl
import com.side.data.mapper.mapperToRunRecordRequest
import com.side.domain.base.BaseResponse
import com.side.domain.model.AllRunRecord
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
    ): Flow<ResultType<BaseResponse<String>>> = flow {
        emit(ResultType.Loading)
        runningRemoteDataSourceImpl.postRunRecord(challengeSeq, allRunRecord.mapperToRunRecordRequest()).collect {
            /** 성공, 실패 나누기 **/
            emit(ResultType.Success(it))
        }
    }.catch {
        emit(ResultType.Error(it))
    }

}