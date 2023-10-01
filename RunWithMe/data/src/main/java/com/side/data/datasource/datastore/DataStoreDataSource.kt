package com.side.data.datasource.datastore

import com.side.domain.model.User
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow

interface DataStoreDataSource {

    suspend fun saveUser(user: User)

    fun getUserID(): Flow<String>

    fun getUserSeq(): Flow<String>

    fun getUserWeight(): Flow<Int>

    suspend fun saveToken(jwt: String, refreshToken: String)

    fun getJWT(): Flow<String>

    fun getRefreshToken(): Flow<String>

    suspend fun saveRunningChallengSeq(challengSeq: Int)

    suspend fun saveRunningGoalAmount(goalAmount: Long)

    suspend fun saveRunningGoalType(goalType: String)

    fun getRunningChallengeSeq(): Flow<Int>

    fun getRunningGoalAmount(): Flow<Long>

    fun getRunningGoalType(): Flow<String>
    suspend fun saveTTSOption(option: Boolean)

    fun getTTSOption(): Flow<Boolean>
}