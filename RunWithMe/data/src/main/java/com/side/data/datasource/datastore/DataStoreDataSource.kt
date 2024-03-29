package com.side.data.datasource.datastore

import com.side.domain.model.User
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow

interface DataStoreDataSource {

    suspend fun saveUser(user: User)

    fun getUserEmail(): Flow<String>

    fun getUserSeq(): Flow<String>

    fun getUserWeight(): Flow<Int>

    suspend fun saveTokenWhenSocialJoin(jwt: String)

    suspend fun saveToken(jwt: String, refreshToken: String)

    suspend fun saveTokenExpired(expired: Long)

    fun getTokenWhenSocialJon(): Flow<String>

    fun getTokenExpired(): Flow<Long>

    fun getJWT(): Flow<String>

    fun getRefreshToken(): Flow<String>

    suspend fun saveRunningChallengSeq(challengSeq: Long)

    suspend fun saveRunningGoalAmount(goalAmount: Long)

    suspend fun saveRunningGoalType(goalType: Int)

    suspend fun saveRunningChallengeName(challengeName: String)

    fun getRunningChallengeSeq(): Flow<Long>

    fun getRunningGoalAmount(): Flow<Long>

    fun getRunningGoalType(): Flow<Int>

    fun getRunningChallengeName(): Flow<String>

    suspend fun saveTTSOption(option: Boolean)

    fun getTTSOption(): Flow<Boolean>

    suspend fun savePemissionCheck(isCheck: Boolean)

    fun getPermissionCheck(): Flow<Boolean>
}