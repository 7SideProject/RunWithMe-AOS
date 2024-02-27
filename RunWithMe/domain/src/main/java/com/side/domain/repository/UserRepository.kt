package com.side.domain.repository

import com.side.domain.base.BaseResponse
import com.side.domain.model.DailyCheck
import com.side.domain.model.DuplicateCheck
import com.side.domain.model.Profile
import com.side.domain.model.SocialLoginUser
import com.side.domain.model.TotalRecord
import com.side.domain.model.User
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

typealias UserTypeResponse = ResultType<BaseResponse<User?>>
typealias DuplicateCheckTypeResponse = ResultType<BaseResponse<DuplicateCheck>>
typealias DailyCheckTypeResponse = ResultType<BaseResponse<DailyCheck?>>
typealias TotalRecordTypeResponse = ResultType<BaseResponse<TotalRecord?>>
typealias SocialLoginResponse = ResultType<BaseResponse<SocialLoginUser?>>

interface UserRepository {
    //    fun login(code: String, state: String): Flow<UserResponse>
    fun join(user: User): Flow<UserTypeResponse>

    fun loginWithEmail(user: User): Flow<UserTypeResponse>

    fun loginWithKakako(accessToken: String): Flow<SocialLoginResponse>

    fun getUserProfile(userSeq: Long): Flow<UserTypeResponse>

    fun changePassword(email: String, password: String): Flow<NullResponse>

    fun checkIdIsDuplicate(email: String): Flow<DuplicateCheckTypeResponse>

    fun checkNicknameIsDuplicate(nickname: String): Flow<DuplicateCheckTypeResponse>

    fun dailyCheck(userSeq: Long): Flow<DailyCheckTypeResponse>

    fun getTotalRecord(userSeq: Long): Flow<TotalRecordTypeResponse>

    fun editProfile(userSeq: Long, editProfileRequest: Profile): Flow<UserTypeResponse>

    fun deleteUser(): Flow<NullResponse>

    fun editProfileImage(
        userSeq: Long,
        image: MultipartBody.Part
    ): Flow<ResultType<BaseResponse<Any?>?>>

    fun joinSocialUser(
        userSeq: Long,
        profile: Profile
    ): Flow<UserTypeResponse>
}