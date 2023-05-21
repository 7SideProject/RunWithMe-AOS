package com.side.data.repository

import android.util.Log
import com.side.data.datasource.UserRemoteDataSource
import com.side.data.mapper.mapperToEmailLoginRequest
import com.side.data.mapper.mapperToJoinRequest
import com.side.data.mapper.mapperToUser
import com.side.data.model.request.LoginRequest
import com.side.domain.base.BaseResponse
import com.side.domain.model.User
import com.side.domain.repository.UserRepository
import com.side.domain.repository.UserResponse
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // 알아보기
class UserRepositoryImpl @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource
) : UserRepository {

    override fun login(code: String, state: String): Flow<UserResponse> = flow {
        emit(ResultType.Loading)
        userRemoteDataSource.login(LoginRequest(code, state)).collectLatest {
            emit(
                ResultType.Success(
                    BaseResponse(
                        it.code,
                        it.message,
                        it.data
                    )
                )
            )


        }
    }.catch {
        emit(
            ResultType.Error(
                it.cause!!
            )
        )
    }

    override fun join(user: User): Flow<UserResponse> = flow {
        emit(ResultType.Loading)
        userRemoteDataSource.join(user.mapperToJoinRequest()).collect {
            Log.d("test123", "join code: ${it.code}")
            when (it.code) {
                "U002" -> {
                    Log.d("test123", "join U002:")
                    emit(
                        ResultType.Success(
                            BaseResponse(
                                it.code,
                                it.message,
                                it.data.mapperToUser()
                            )
                        )
                    )
                }
                "U101" -> {
                    Log.d("test123", "join code: ${it.code}")
                    emit(
                        ResultType.Fail(
                            BaseResponse(
                                it.code,
                                it.message,
                                it.data.mapperToUser()
                            )
                        )
                    )
                }
            }


        }
    }.catch {
        emit(
            ResultType.Error(
                it.cause!!
            )
        )
    }

    override fun loginWithEmail(user: User): Flow<UserResponse> = flow {
        emit(ResultType.Loading)
        userRemoteDataSource.loginWithEmail(user.mapperToEmailLoginRequest()).collect {
            when (it.code) {
                "U001" -> {
                    emit(
                        ResultType.Success(
                            BaseResponse(
                                it.code,
                                it.message,
                                it.data.mapperToUser()
                            )
                        )
                    )
                }
                "U102" -> {
                    emit(
                        ResultType.Fail(
                            BaseResponse(
                                it.code,
                                it.message,
                                it.data.mapperToUser()
                            )
                        )
                    )
                }
            }
        }
    }.catch {
        emit(
            ResultType.Error(
                it.cause!!
            )
        )
    }


}
