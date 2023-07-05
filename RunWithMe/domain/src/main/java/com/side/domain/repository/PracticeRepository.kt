package com.side.domain.repository

import com.side.domain.modedl.PracticeRunRecord
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow

typealias ResultPracticeRunRecords = ResultType<List<PracticeRunRecord>>
typealias ResultPracticeInt = ResultType<Int>
typealias ResultPracticeDouble = ResultType<Double>
typealias ResultPracticeBoolean = ResultType<Boolean>

interface PracticeRepository {

    suspend fun insertRunRecord(run : PracticeRunRecord): Flow<ResultPracticeBoolean>

    suspend fun deleteRunRecord(run: PracticeRunRecord): Flow<ResultPracticeBoolean>

    fun getAllRunRecord(): Flow<ResultPracticeRunRecords>

    fun getTotalTimeInMillis(): Flow<ResultPracticeInt>

    fun getTotalDistance(): Flow<ResultPracticeInt>

    fun getTotalAvgSpeed(): Flow<ResultPracticeDouble>

    fun getTotalCaloriesBurned(): Flow<ResultPracticeInt>

}