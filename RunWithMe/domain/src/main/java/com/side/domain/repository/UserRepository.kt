package com.side.domain.repository

import com.side.domain.base.BaseResponse
import com.side.domain.model.DailyCheck
import com.side.domain.model.DuplicateCheck
import com.side.domain.model.Profile
import com.side.domain.model.TotalRecord
import com.side.domain.model.User
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow

typealias UserTypeResponse = ResultType<BaseResponse<User?>>
typealias NullResponse = ResultType<BaseResponse<Any?>>
typealias DuplicateCheckTypeResponse = ResultType<BaseResponse<DuplicateCheck>>
typealias DailyCheckTypeResponse = ResultType<BaseResponse<DailyCheck?>>
typealias TotalRecordTypeResponse = ResultType<BaseResponse<TotalRecord?>>

interface UserRepository {
    //    fun login(code: String, state: String): Flow<UserResponse>
    fun join(user: User): Flow<UserTypeResponse>

    fun loginWithEmail(user: User): Flow<UserTypeResponse>

    fun getUserProfile(userSeq: Long): Flow<UserTypeResponse>

    fun changePassword(email: String, password: String): Flow<NullResponse>

    fun checkIdIsDuplicate(email: String): Flow<DuplicateCheckTypeResponse>

    fun checkNicknameIsDuplicate(nickname: String): Flow<DuplicateCheckTypeResponse>

    fun dailyCheck(userSeq: Long): Flow<DailyCheckTypeResponse>

    fun getTotalRecord(userSeq: Long): Flow<TotalRecordTypeResponse>

    fun editProfile(userSeq: Long, editProfileRequest: Profile): Flow<UserTypeResponse>

    fun deleteUser(): Flow<NullResponse>
}