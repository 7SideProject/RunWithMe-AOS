package com.side.data.datasource.local

import com.side.data.db.RunDao
import com.side.data.entity.RunRecordEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PracticeLocalDataSourceImpl @Inject constructor(
    private val runDao: RunDao
): PracticeLocalDataSource{

    override suspend fun insertRunRecord(run: RunRecordEntity) = runDao.insertRun(run)

    override suspend fun deleteRunRecord(run: RunRecordEntity) = runDao.deleteRun(run)

    override fun getAllRunRecord(): List<RunRecordEntity> = runDao.getAllRunsSortedByDate()

    override fun getTotalTimeInMillis(): Int = runDao.getTotalTimeInMillis()

    override fun getTotalDistance(): Int = runDao.getTotalDistance()

    override fun getTotalAvgSpeed(): Double = runDao.getTotalAvgSpeed()

    override fun getTotalCaloriesBurned(): Int = runDao.getTotalCaloriesBurned()

}