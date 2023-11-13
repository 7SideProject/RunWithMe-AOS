package com.side.data.datasource.user

import com.side.data.api.LoginApi
import com.side.data.api.UserApi
import com.side.data.model.request.EmailLoginRequest
import com.side.data.model.request.FindPasswordRequest
import com.side.data.model.request.JoinRequest
import com.side.data.model.request.LoginRequest
import com.side.data.model.response.DailyCheckResponse
import com.side.data.model.response.DuplicateCheckResponse
import com.side.data.model.response.EmailLoginResponse
import com.side.data.model.response.TotalRecordResponse
import com.side.data.model.response.UserResponse
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

    override fun join(joinRequest: JoinRequest): Flow<BaseResponse<UserResponse>> = flow {
        emit(userApi.join(joinRequest))
    }

    override fun loginWithEmail(emailLoginRequest: EmailLoginRequest): Flow<BaseResponse<EmailLoginResponse?>> =
        flow {
            emit(loginApi.loginWithEmail(emailLoginRequest))
        }

    override fun getUserProfile(userSeq: Long): Flow<BaseResponse<UserResponse>> = flow {
        emit(userApi.getUserProfile(userSeq))
    }

    override fun checkIdIsDuplicate(email: String): Flow<BaseResponse<DuplicateCheckResponse>> = flow {
        emit(userApi.checkIdIsDuplicate(email))
    }

    override fun checkNicknameIsDuplicate(nickname: String): Flow<BaseResponse<DuplicateCheckResponse>> = flow {
        emit(userApi.checkNicknameIsDuplicate(nickname))
    }

    override fun changePassword(email: String, passwordRequest: FindPasswordRequest): Flow<BaseResponse<Any?>> = flow {
        emit(userApi.changePassword(email, passwordRequest))
    }

    override fun dailyCheck(userSeq: Long): Flow<BaseResponse<DailyCheckResponse>> = flow {
        emit(userApi.dailyCheck(userSeq))
    }

    override fun getTotalRecord(userSeq: Long): Flow<BaseResponse<TotalRecordResponse>> = flow {
        emit(userApi.getTotalRecord(userSeq))
    }
}