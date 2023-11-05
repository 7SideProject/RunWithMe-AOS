package com.side.domain.repository

import com.side.domain.base.BaseResponse
import com.side.domain.model.DailyCheck
import com.side.domain.model.DuplicateCheck
import com.side.domain.model.TotalRecord
import com.side.domain.model.User
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow

typealias UserResponse = ResultType<BaseResponse<User?>>
typealias DuplicateCheckTypeResponse = ResultType<BaseResponse<DuplicateCheck>>
typealias DailyCheckTypeResponse = ResultType<BaseResponse<DailyCheck?>>
typealias TotalRecordTypeResponse = ResultType<BaseResponse<TotalRecord?>>

interface UserRepository {
    //    fun login(code: String, state: String): Flow<UserResponse>
    fun join(user: User): Flow<UserResponse>

    fun loginWithEmail(user: User): Flow<UserResponse>

    fun getUserProfile(userSeq: Long): Flow<UserResponse>

    fun checkIdIsDuplicate(email: String): Flow<DuplicateCheckTypeResponse>

    fun checkNicknameIsDuplicate(nickname: String): Flow<DuplicateCheckTypeResponse>

    fun dailyCheck(userSeq: Long): Flow<DailyCheckTypeResponse>

    fun getTotalRecord(userSeq: Long): Flow<TotalRecordTypeResponse>
}