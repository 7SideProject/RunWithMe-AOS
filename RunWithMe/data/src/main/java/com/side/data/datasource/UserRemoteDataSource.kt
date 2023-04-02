package com.side.data.datasource

import com.side.data.api.UserApi
import com.side.data.model.request.LoginRequest
import com.side.domain.base.BaseResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRemoteDataSource @Inject constructor(
    private val userApi: UserApi
) {
    fun login(loginRequest: LoginRequest): Flow<BaseResponse<String>> = flow {
        emit(userApi.login(loginRequest.code, loginRequest.state))
    }
}