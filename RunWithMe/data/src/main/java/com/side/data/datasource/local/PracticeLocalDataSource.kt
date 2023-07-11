package com.side.data.datasource.local

import com.side.data.entity.RunRecordEntity
import kotlinx.coroutines.flow.Flow

interface PracticeLocalDataSource {

    suspend fun insertRunRecord(run : RunRecordEntity): Long

    suspend fun deleteRunRecord(run: RunRecordEntity): Int

    fun getAllRunRecord(): List<RunRecordEntity>

    fun getTotalTimeInMillis(): Int

    fun getTotalDistance(): Int

    fun getTotalAvgSpeed(): Double

    fun getTotalCaloriesBurned(): Int
}
