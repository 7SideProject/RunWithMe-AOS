package com.side.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.side.data.entity.RunRecordEntity

@Dao
interface RunDao {

    // 기록 추가
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRun(run: RunRecordEntity): Long

    // 날짜 순으로 정렬
    @Query("SELECT * FROM run_table ORDER BY seq DESC")
    fun getAllRunsSortedByDate(): List<RunRecordEntity>

    // 기록 삭제
    @Delete
    fun deleteRun(run: RunRecordEntity): Long

    // 시간 합계
    @Query("SELECT SUM(runningTime) FROM run_table")
    fun getTotalTimeInMillis(): Int

    // 거리 합계
    @Query("SELECT SUM(runningDistance) FROM run_table")
    fun getTotalDistance(): Int

    // 평균 속도
    @Query("SELECT AVG(avgSpeed) FROM run_table")
    fun getTotalAvgSpeed(): Double

    // 칼로리 합계
    @Query("SELECT SUM(calorie) FROM run_table")
    fun getTotalCaloriesBurned(): Int

}