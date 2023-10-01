package com.side.data.datasource.user

import com.side.data.api.LoginApi
import com.side.data.api.UserApi
import com.side.data.model.request.EmailLoginRequest
import com.side.data.model.request.JoinRequest
import com.side.data.model.request.LoginRequest
import com.side.data.model.response.EmailLoginResponse
import com.side.data.model.response.JoinResponse
import com.side.domain.base.BaseResponse
import com.side.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRemoteDataSourceImpl @Inject constructor(
    private val userApi: UserApi,
    private val loginApi: LoginApi
): UserRemoteDataSource {
    override fun login(loginRequest: LoginRequest): Flow<BaseResponse<User>> = flow {
        emit(userApi.login(loginRequest.code, loginRequest.state))
    }

    override fun join(joinRequest: JoinRequest): Flow<BaseResponse<JoinResponse>> = flow {
        emit(userApi.join(joinRequest))
    }

    override fun loginWithEmail(emailLoginRequest: EmailLoginRequest): Flow<BaseResponse<EmailLoginResponse?>> =
        flow {
            emit(loginApi.loginWithEmail(emailLoginRequest))
        }

}