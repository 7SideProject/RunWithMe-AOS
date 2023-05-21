package com.side.data.datasource

import android.util.Log
import com.side.data.api.UserApi
import com.side.data.model.request.JoinRequest
import com.side.data.model.request.LoginRequest
import com.side.data.model.response.JoinResponse
import com.side.domain.base.BaseResponse
import com.side.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRemoteDataSource @Inject constructor(
    private val userApi: UserApi
) {
    fun login(loginRequest: LoginRequest): Flow<BaseResponse<User>> = flow {
        emit(userApi.login(loginRequest.code, loginRequest.state))
    }

    fun join(joinRequest: JoinRequest): Flow<BaseResponse<JoinResponse>> = flow {
        Log.d("test123", "UserRemoteDataSource join: ")
        emit(userApi.join(joinRequest))
    }
}