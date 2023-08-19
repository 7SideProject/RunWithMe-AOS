package com.side.data.repository

import android.util.Log
import com.side.data.datasource.datastore.DataStoreDataSource
import com.side.data.datasource.user.UserRemoteDataSource
import com.side.data.mapper.mapperToEmailLoginRequest
import com.side.data.mapper.mapperToJoinRequest
import com.side.data.mapper.mapperToUser
import com.side.data.model.request.LoginRequest
import com.side.data.model.response.EmailLoginResponse
import com.side.data.util.ResponseCodeStatus
import com.side.data.util.emitResultTypeError
import com.side.data.util.emitResultTypeFail
import com.side.data.util.emitResultTypeSuccess
import com.side.data.util.initKeyStore
import com.side.domain.base.BaseResponse
import com.side.domain.base.changeData
import com.side.domain.base.changeMessageAndData
import com.side.domain.model.User
import com.side.domain.repository.UserRepository
import com.side.domain.repository.UserResponse
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // 알아보기
class UserRepositoryImpl @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val dataStoreDataSource: DataStoreDataSource,
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
            when (it.code) {
                ResponseCodeStatus.USER_REQUEST_SUCCESS.code -> {
                    emitResultTypeSuccess(
                        it.changeData(
                            it.data.mapperToUser()
                        )
                    )
                }
                ResponseCodeStatus.EMAIL_EXISTS.code -> {
                    emitResultTypeFail(
                        it.changeMessageAndData(
                            ResponseCodeStatus.EMAIL_EXISTS.message,
                            null
                        )
                    )
                }
                else -> {

                    emitResultTypeFail(it.changeData(null))

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
            Log.d("test123", "loginWithEmail: ${it.data}")

            when (it.code) {
                ResponseCodeStatus.LOGIN_SUCCESS.code -> {
                    val userResponse = (it.data as EmailLoginResponse).mapperToUser()

                    initKeyStore(Calendar.getInstance().timeInMillis.toString())
                    /** User Info DataStore 저장 **/
                    saveUserInfo(userResponse)

                    emitResultTypeSuccess(
                        it.changeMessageAndData(
                            ResponseCodeStatus.LOGIN_SUCCESS.message,
                            userResponse
                        )
                    )

                }
                ResponseCodeStatus.USER_NOT_FOUND.code -> {
                    emitResultTypeFail(
                        it.changeMessageAndData(
                            ResponseCodeStatus.USER_NOT_FOUND.message,
                            null
                        )
                    )
                }
                ResponseCodeStatus.BAD_PASSWORD.code -> {
                    emitResultTypeFail(
                        it.changeMessageAndData(
                            ResponseCodeStatus.BAD_PASSWORD.message,
                            null
                        )
                    )
                }
                else -> {
                    emitResultTypeFail(
                        it.changeData(null)
                    )

                }
            }
        }
    }.catch {
        emitResultTypeError(it.cause!!)
    }

    suspend fun saveUserInfo(user: User) {
        dataStoreDataSource.saveUser(user)
    }

}
