package com.side.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.side.data.converter.TypeConverter
import com.side.data.entity.RunRecordEntity

/* entities = 사용할 엔티티 선언, version = 엔티티 구조 변경 시 구분해주는 역할
   exportSchema = 스키마 내보내기 설정 */
@Database(entities = [RunRecordEntity::class], version = 1, exportSchema = false)
@TypeConverters(TypeConverter::class)
abstract class RunDatabase : RoomDatabase() {

    abstract fun runDao() : RunDao
}