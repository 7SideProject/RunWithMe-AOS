package com.side.data.datasource.user

import com.side.data.model.request.EmailLoginRequest
import com.side.data.model.request.JoinRequest
import com.side.data.model.request.LoginRequest
import com.side.data.model.response.DuplicateCheckResponse
import com.side.data.model.response.EmailLoginResponse
import com.side.data.model.response.UserResponse
import com.side.domain.base.BaseResponse
import com.side.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRemoteDataSource {
    fun login(loginRequest: LoginRequest): Flow<BaseResponse<User>>

    fun join(joinRequest: JoinRequest): Flow<BaseResponse<UserResponse>>

    fun loginWithEmail(emailLoginRequest: EmailLoginRequest): Flow<BaseResponse<EmailLoginResponse?>>

    fun getUserProfile(userSeq: Long): Flow<BaseResponse<UserResponse>>

    fun checkIdIsDuplicate(email: String): Flow<BaseResponse<DuplicateCheckResponse>>

    fun checkNicknameIsDuplicate(nickname: String): Flow<BaseResponse<DuplicateCheckResponse>>

    fun changePassword(email: String, password: String): Flow<BaseResponse<Any?>>
}