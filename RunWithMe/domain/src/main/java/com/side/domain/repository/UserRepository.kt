package com.side.domain.repository

import com.side.domain.base.BaseResponse
import com.side.domain.model.DuplicateCheck
import com.side.domain.model.User
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow

typealias UserResponse = ResultType<BaseResponse<User?>>
typealias DuplicateCheckResponse = ResultType<BaseResponse<DuplicateCheck>>

interface UserRepository {
//    fun login(code: String, state: String): Flow<UserResponse>
    fun join(user: User): Flow<UserResponse>

    fun loginWithEmail(user: User) : Flow<UserResponse>

    fun getUserProfile(userSeq: Long): Flow<UserResponse>

    fun checkIdIsDuplicate(email: String): Flow<DuplicateCheckResponse>
    fun checkNicknameIsDuplicate(nickname: String): Flow<DuplicateCheckResponse>
}