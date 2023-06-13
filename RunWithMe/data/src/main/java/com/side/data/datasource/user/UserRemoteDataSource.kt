package com.side.data.datasource.user

import com.side.data.model.request.EmailLoginRequest
import com.side.data.model.request.JoinRequest
import com.side.data.model.request.LoginRequest
import com.side.data.model.response.EmailLoginResponse
import com.side.data.model.response.JoinResponse
import com.side.domain.base.BaseResponse
import com.side.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface UserRemoteDataSource {
    fun login(loginRequest: LoginRequest): Flow<BaseResponse<User>>

    fun join(joinRequest: JoinRequest): Flow<BaseResponse<JoinResponse>>

    fun loginWithEmail(emailLoginRequest: EmailLoginRequest): Flow<BaseResponse<EmailLoginResponse?>>

}