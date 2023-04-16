package com.side.data.repository

import android.util.Log
import com.side.data.datasource.UserRemoteDataSource
import com.side.data.model.request.LoginRequest
import com.side.domain.base.BaseResponse
import com.side.domain.repository.UserRepository
import com.side.domain.repository.UserResponse
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import retrofit2.http.HTTP
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.log

@Singleton // 알아보기
class UserRepositoryImpl @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource
): UserRepository {

    override fun login(code: String, state: String): Flow<UserResponse> = flow {
        emit(ResultType.Loading)
        userRemoteDataSource.login(LoginRequest(code, state)).collectLatest{
                emit(
                    ResultType.Success(
                        BaseResponse(
                            it.message,
                            it.data
                        )
                    )
                )


        }
    }.catch {
        Log.d("eettt", "login: ${it.message}")
        Log.d("eettt", "login: ${it.cause}")
        emit(ResultType.Error(
            it.cause!!
        ))
    }

}
