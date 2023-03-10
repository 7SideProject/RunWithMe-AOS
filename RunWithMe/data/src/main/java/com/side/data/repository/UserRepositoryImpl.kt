package com.side.data.repository

import com.side.data.datasource.UserRemoteDataSource
import com.side.data.mapper.mapperToUser
import com.side.data.model.request.LoginRequest
import com.side.domain.base.BaseResponse
import com.side.domain.repository.UserRepository
import com.side.domain.repository.UserResponse
import com.side.domain.utils.ResultType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // 알아보기
class UserRepositoryImpl @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource
): UserRepository {

    override fun loginUser(accessToken: String): Flow<UserResponse> = flow {
        emit(ResultType.Loading)
        userRemoteDataSource.loginUser(LoginRequest(accessToken)).collectLatest{
            emit(
                ResultType.Success(
                    BaseResponse(
                        it.message,
                        it.data.mapperToUser()
                    )
                )
            )
        }
    }.catch {
        emit(ResultType.Error(
            it
        ))
    }.shareIn(
        CoroutineScope(Dispatchers.IO),
        replay = 1,
        started = SharingStarted.WhileSubscribed(5000)
    )


}