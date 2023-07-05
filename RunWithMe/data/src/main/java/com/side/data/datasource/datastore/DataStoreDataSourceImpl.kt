package com.side.data.datasource.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.side.data.util.*
import com.side.data.util.preferencesKeys.EMAIL
import com.side.data.util.preferencesKeys.JWT
import com.side.data.util.preferencesKeys.REFRESH_TOKEN
import com.side.data.util.preferencesKeys.SEQ
import com.side.data.util.preferencesKeys.WEIGHT
import com.side.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

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
        emit(dataStore.getValue(WEIGHT, DATASTORE_KEY_TYPE_STRING).first().toString().toInt())
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

}