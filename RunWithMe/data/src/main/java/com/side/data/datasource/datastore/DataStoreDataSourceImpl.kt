package com.side.data.datasource.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.side.data.util.DATASTORE_KEY
import com.side.data.util.getDecryptStringValue
import com.side.data.util.getValue
import com.side.data.util.preferencesKeys.CHALLENGE_NAME
import com.side.data.util.preferencesKeys.CHALLENGE_SEQ
import com.side.data.util.preferencesKeys.EMAIL
import com.side.data.util.preferencesKeys.GOAL_AMOUNT
import com.side.data.util.preferencesKeys.GOAL_TYPE
import com.side.data.util.preferencesKeys.JWT
import com.side.data.util.preferencesKeys.REFRESH_TOKEN
import com.side.data.util.preferencesKeys.SEQ
import com.side.data.util.preferencesKeys.TTS_OPTION
import com.side.data.util.preferencesKeys.WEIGHT
import com.side.data.util.saveEncryptStringValue
import com.side.data.util.saveValue
import com.side.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreDataSourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
): DataStoreDataSource {

    override suspend fun saveUser(user: User) {
        dataStore.saveEncryptStringValue(EMAIL, user.email)
        dataStore.saveEncryptStringValue(SEQ, user.seq.toString())
        dataStore.saveValue(WEIGHT, user.weight)
    }

    override fun getUserEmail(): Flow<String> = flow {
        emit(dataStore.getDecryptStringValue(EMAIL).first().toString())
    }

    override fun getUserSeq(): Flow<String> = flow {
        emit(dataStore.getDecryptStringValue(SEQ).first().toString())
    }

    override fun getUserWeight(): Flow<Int> = flow {
        emit(dataStore.getValue(WEIGHT, DATASTORE_KEY.TYPE_STRING).first().toString().toInt())
    }

    override suspend fun saveToken(jwt: String, refreshToken: String) {
        dataStore.saveEncryptStringValue(JWT, jwt)
        dataStore.saveEncryptStringValue(REFRESH_TOKEN, refreshToken)
    }

    override fun getJWT(): Flow<String> = flow {
        emit(dataStore.getDecryptStringValue(JWT).first().toString())
    }

    override fun getRefreshToken(): Flow<String> = flow {
        emit(dataStore.getDecryptStringValue(REFRESH_TOKEN).first().toString())
    }

    override suspend fun saveRunningChallengSeq(challengSeq: Int) {
        dataStore.saveValue(CHALLENGE_SEQ, challengSeq)
    }

    override suspend fun saveRunningGoalAmount(goalAmount: Long) {
        dataStore.saveValue(GOAL_AMOUNT, goalAmount)
    }

    override suspend fun saveRunningGoalType(goalType: Int) {
        dataStore.saveValue(GOAL_TYPE, goalType)
    }

    override suspend fun saveRunningChallengeName(challengeName: String) {
        dataStore.saveValue(CHALLENGE_NAME, challengeName)
    }

    override fun getRunningChallengeSeq(): Flow<Int> = flow {
        emit(dataStore.getValue(CHALLENGE_SEQ, DATASTORE_KEY.TYPE_INT).first().toString().toInt())
    }

    override fun getRunningGoalAmount(): Flow<Long> = flow {
        emit(dataStore.getValue(GOAL_AMOUNT, DATASTORE_KEY.TYPE_LONG).first().toString().toLong())
    }

    override fun getRunningGoalType(): Flow<Int> = flow {
        emit(dataStore.getValue(GOAL_TYPE, DATASTORE_KEY.TYPE_STRING).first().toString().toInt())
    }

    override fun getRunningChallengeName(): Flow<String> = flow {
        emit(dataStore.getValue(CHALLENGE_NAME, DATASTORE_KEY.TYPE_STRING).first().toString())
    }

    override suspend fun saveTTSOption(option: Boolean) {
        dataStore.saveValue(TTS_OPTION, option)
    }

    override fun getTTSOption(): Flow<Boolean> = flow {
        emit(dataStore.getValue(TTS_OPTION, DATASTORE_KEY.TYPE_BOOLEAN).first().toString().toBoolean())
    }
}