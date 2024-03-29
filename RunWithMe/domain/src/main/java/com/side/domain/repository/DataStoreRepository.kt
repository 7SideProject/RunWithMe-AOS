package com.side.domain.repository

import com.side.domain.model.RunningInfo
import com.side.domain.model.User
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {

    suspend fun saveUser(user: User)

    fun getUserEmail(): Flow<ResultType<String>>

    fun getUserSeq(): Flow<ResultType<Long>>

    fun getUserWeight(): Flow<ResultType<Int>>

    suspend fun saveToken(jwt: String, refreshToken: String)

    fun getJWT(): Flow<String>

//    fun getRefreshToken(): Flow<String>

    suspend fun saveRunningChallengeSeq(challengeSeq: Long)

    suspend fun saveRunningGoalAmount(goalAmount: Long)

    suspend fun saveRunningGoalType(goalType: Int)

    suspend fun saveRunningChallengeName(challengeName: String)

    fun getRunningChallengInfo() : Flow<ResultType<RunningInfo>>

    suspend fun saveTTSOption(option: Boolean)

    fun getTTSOption(): Flow<Boolean>

    suspend fun savePermissionCheck(isCheck: Boolean)

    fun getPermissionCHeck(): Flow<Boolean>
}