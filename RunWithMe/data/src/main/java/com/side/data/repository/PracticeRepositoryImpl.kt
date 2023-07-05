package com.side.data.repository

import com.side.data.datasource.local.PracticeLocalDataSource
import com.side.data.mapper.mapperToPracitceRunRecord
import com.side.data.mapper.mapperToRunRecordEntity
import com.side.data.util.emitResultTypeError
import com.side.data.util.emitResultTypeFail
import com.side.data.util.emitResultTypeLoading
import com.side.data.util.emitResultTypeSuccess
import com.side.domain.modedl.PracticeRunRecord
import com.side.domain.repository.PracticeRepository
import com.side.domain.repository.ResultPracticeBoolean
import com.side.domain.repository.ResultPracticeDouble
import com.side.domain.repository.ResultPracticeInt
import com.side.domain.repository.ResultPracticeRunRecords
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PracticeRepositoryImpl @Inject constructor(
    private val practiceLocalDataSource: PracticeLocalDataSource
): PracticeRepository{
    override suspend fun insertRunRecord(run: PracticeRunRecord): Flow<ResultPracticeBoolean> = flow {
        emitResultTypeLoading()
        if(practiceLocalDataSource.insertRunRecord(run.mapperToRunRecordEntity()) > 0){
            emitResultTypeSuccess(true)
        }else{
            emitResultTypeFail(false)
        }
    }.catch {
        emitResultTypeError(it)
    }

    override suspend fun deleteRunRecord(run: PracticeRunRecord): Flow<ResultPracticeBoolean> = flow {
        emitResultTypeLoading()
        if(practiceLocalDataSource.deleteRunRecord(run.mapperToRunRecordEntity()) > 0){
            emitResultTypeSuccess(true)
        }else{
            emitResultTypeFail(false)
        }
    }.catch {
        emitResultTypeError(it)
    }

    override fun getAllRunRecord(): Flow<ResultPracticeRunRecords> = flow {
        emitResultTypeLoading()
        val list = practiceLocalDataSource.getAllRunRecord()
        emitResultTypeSuccess(list.mapperToPracitceRunRecord())
    }.catch {
        emitResultTypeError(it)
    }

    override fun getTotalTimeInMillis(): Flow<ResultPracticeInt> = flow {
        emitResultTypeLoading()
        emitResultTypeSuccess(practiceLocalDataSource.getTotalTimeInMillis())
    }.catch {
        emitResultTypeError(it)
    }

    override fun getTotalDistance(): Flow<ResultPracticeInt>  = flow {
        emitResultTypeLoading()
        emitResultTypeSuccess(practiceLocalDataSource.getTotalDistance())
    }.catch {
        emitResultTypeError(it)
    }

    override fun getTotalAvgSpeed(): Flow<ResultPracticeDouble>  = flow {
        emitResultTypeLoading()
        emitResultTypeSuccess(practiceLocalDataSource.getTotalAvgSpeed())
    }.catch {
        emitResultTypeError(it)
    }
    override fun getTotalCaloriesBurned(): Flow<ResultPracticeInt>  = flow {
        emitResultTypeLoading()
        emitResultTypeSuccess(practiceLocalDataSource.getTotalCaloriesBurned())
    }.catch {
        emitResultTypeError(it)
    }


}