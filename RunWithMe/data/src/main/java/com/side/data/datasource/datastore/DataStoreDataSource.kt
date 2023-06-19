package com.side.data.datasource.datastore

import com.side.domain.model.User
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow

interface DataStoreDataSource {

    suspend fun saveUser(user: User)

    fun getUserEmail(): Flow<String>

    fun getUserSeq(): Flow<Long>

    fun getUserWeight(): Flow<Int>

    suspend fun saveToken(jwt: String, refreshToken: String)

    fun getJWT(): Flow<String>

    fun getRefreshToken(): Flow<String>

}