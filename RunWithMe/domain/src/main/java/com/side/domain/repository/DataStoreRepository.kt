package com.side.domain.repository

import com.side.domain.model.User
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {

    suspend fun saveUser(user: User)

    fun getUserEmail(): Flow<ResultType<String>>

    fun getUserSeq(): Flow<ResultType<Long>>

    fun getUserWeight(): Flow<ResultType<Int>>

    suspend fun saveToken(jwt: String, refreshToken: String)

//    fun getJWT(): Flow<ResultType<String>>
//
//    fun getRefreshToken(): Flow<ResultType<String>>

}