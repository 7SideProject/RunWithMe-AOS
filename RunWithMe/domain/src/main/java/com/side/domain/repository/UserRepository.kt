package com.side.domain.repository

import com.side.domain.base.BaseResponse
import com.side.domain.model.DailyCheck
import com.side.domain.model.DuplicateCheck
import com.side.domain.model.User
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow

typealias UserResponse = ResultType<BaseResponse<User?>>

typealias NullResponse = ResultType<BaseResponse<Any?>>
typealias DuplicateCheckTypeResponse = ResultType<BaseResponse<DuplicateCheck>>
typealias DailyCheckTypeResponse = ResultType<BaseResponse<DailyCheck?>>

interface UserRepository {
    //    fun login(code: String, state: String): Flow<UserResponse>
    fun join(user: User): Flow<UserResponse>

    fun loginWithEmail(user: User): Flow<UserResponse>

    fun getUserProfile(userSeq: Long): Flow<UserResponse>

    fun changePassword(email: String, password: String): Flow<NullResponse>

    fun checkIdIsDuplicate(email: String): Flow<DuplicateCheckTypeResponse>

    fun checkNicknameIsDuplicate(nickname: String): Flow<DuplicateCheckTypeResponse>

    fun dailyCheck(userSeq: Long): Flow<DailyCheckTypeResponse>
}