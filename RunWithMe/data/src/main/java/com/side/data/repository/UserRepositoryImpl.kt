package com.side.data.repository

import com.side.data.datasource.datastore.DataStoreDataSource
import com.side.data.datasource.user.UserRemoteDataSource
import com.side.data.mapper.mapperToDailyCheck
import com.side.data.mapper.mapperToDuplicateCheck
import com.side.data.mapper.mapperToEditProfileRequest
import com.side.data.mapper.mapperToEmailLoginRequest
import com.side.data.mapper.mapperToJoinRequest
import com.side.data.mapper.mapperToTotalRecord
import com.side.data.mapper.mapperToUser
import com.side.data.model.request.FindPasswordRequest
import com.side.data.model.response.EmailLoginResponse
import com.side.data.util.ResponseCodeStatus
import com.side.data.util.asResult
import com.side.data.util.asResultOtherType
import com.side.data.util.initKeyStore
import com.side.domain.base.changeData
import com.side.domain.base.changeMessageAndData
import com.side.domain.model.Profile
import com.side.domain.model.User
import com.side.domain.repository.DailyCheckTypeResponse
import com.side.domain.repository.DuplicateCheckTypeResponse
import com.side.domain.repository.NullResponse
import com.side.domain.repository.TotalRecordTypeResponse
import com.side.domain.repository.UserRepository
import com.side.domain.repository.UserTypeResponse
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // 알아보기
class UserRepositoryImpl @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val dataStoreDataSource: DataStoreDataSource,
) : UserRepository {

    override fun join(user: User): Flow<UserTypeResponse> = userRemoteDataSource.join(user.mapperToJoinRequest()).asResultOtherType {
        when(it.code){
            ResponseCodeStatus.USER_REQUEST_SUCCESS.code -> {
                ResultType.Success(it.changeData(it.data.mapperToUser()))
            }
            ResponseCodeStatus.EMAIL_EXISTS.code -> {
                ResultType.Fail(it.changeMessageAndData(ResponseCodeStatus.EMAIL_EXISTS.message, null))
            }
            else -> {
                ResultType.Fail(it.changeData(null))
            }
        }
    }

    override fun loginWithEmail(user: User): Flow<UserTypeResponse> = userRemoteDataSource.loginWithEmail(user.mapperToEmailLoginRequest()).asResultOtherType {
        when(it.code){
            ResponseCodeStatus.LOGIN_SUCCESS.code -> {
                val userResponse = (it.data as EmailLoginResponse).mapperToUser()

                initKeyStore(Calendar.getInstance().timeInMillis.toString())
                /** User Info DataStore 저장 **/
                saveUserInfo(userResponse)

                ResultType.Success(
                    it.changeMessageAndData(
                        ResponseCodeStatus.LOGIN_SUCCESS.message,
                        userResponse
                    )
                )

            }
            ResponseCodeStatus.USER_NOT_FOUND.code -> {
                ResultType.Fail(
                    it.changeMessageAndData(
                        ResponseCodeStatus.USER_NOT_FOUND.message,
                        null
                    )
                )
            }
            ResponseCodeStatus.BAD_PASSWORD.code -> {
                ResultType.Fail(
                    it.changeMessageAndData(
                        ResponseCodeStatus.BAD_PASSWORD.message,
                        null
                    )
                )
            }
            else -> {
                ResultType.Fail(
                    it.changeData(null)
                )
            }
        }
    }


    override fun getUserProfile(userSeq: Long): Flow<UserTypeResponse> = userRemoteDataSource.getUserProfile(userSeq).asResultOtherType{
        when(it.code){
            ResponseCodeStatus.USER_REQUEST_SUCCESS.code -> {
                ResultType.Success(
                    it.changeData(
                        it.data.mapperToUser()
                    )
                )
            }
            ResponseCodeStatus.DELETED_USER.code -> {
                ResultType.Fail(
                    it.changeMessageAndData(
                        ResponseCodeStatus.DELETED_USER.message,
                        null
                    )
                )
            }
            ResponseCodeStatus.SEQ_NOT_FOUND.code -> {
                ResultType.Fail(
                    it.changeMessageAndData(
                        ResponseCodeStatus.SEQ_NOT_FOUND.message,
                        null
                    )
                )
            }
            else -> {
                ResultType.Fail(
                    it.changeData(null)
                )
            }
        }
    }

    suspend fun saveUserInfo(user: User) {
        dataStoreDataSource.saveUser(user)
    }

    override fun checkIdIsDuplicate(email: String): Flow<DuplicateCheckTypeResponse>
        = userRemoteDataSource.checkIdIsDuplicate(email).asResultOtherType {
        when(it.code){
            ResponseCodeStatus.USER_REQUEST_SUCCESS.code -> {
                ResultType.Success(it.changeData(it.data.mapperToDuplicateCheck()))
            }
            // 요청 실패 코드?
            else -> {
                ResultType.Fail(it.changeData(it.data.mapperToDuplicateCheck()))
            }
        }
    }

    override fun checkNicknameIsDuplicate(nickname: String): Flow<DuplicateCheckTypeResponse>
            = userRemoteDataSource.checkNicknameIsDuplicate(nickname).asResultOtherType {
        when(it.code){
            ResponseCodeStatus.USER_REQUEST_SUCCESS.code -> {
                ResultType.Success(it.changeData(it.data.mapperToDuplicateCheck()))
            }
            else -> {
                ResultType.Fail(it.changeData(it.data.mapperToDuplicateCheck()))
            }
        }
    }

    override fun changePassword(email: String, password: String): Flow<NullResponse> = userRemoteDataSource.changePassword(email, FindPasswordRequest(password)).asResult {
        when (it.code) {
            101 -> {
                ResultType.Success(it)
            }

            else -> {
                ResultType.Fail(it)
            }
        }
    }

    override fun dailyCheck(userSeq: Long): Flow<DailyCheckTypeResponse>
        = userRemoteDataSource.dailyCheck(userSeq).asResultOtherType {
        when(it.code){
            ResponseCodeStatus.USER_REQUEST_SUCCESS.code -> {
                ResultType.Success(
                    it.changeData(it.data.mapperToDailyCheck())
                )
            }
            ResponseCodeStatus.SEQ_NOT_FOUND.code -> {
                ResultType.Fail(
                    it.changeMessageAndData(
                        ResponseCodeStatus.SEQ_NOT_FOUND.message,
                        null
                    )
                )
            }
            ResponseCodeStatus.DELETED_USER.code -> {
                ResultType.Fail(
                    it.changeMessageAndData(
                        ResponseCodeStatus.DELETED_USER.message,
                        null
                    )
                )
            }
            ResponseCodeStatus.NOT_RESOURCE_CREATE_USER.code -> {
                ResultType.Fail(
                    it.changeMessageAndData(
                        ResponseCodeStatus.NOT_RESOURCE_CREATE_USER.message,
                        null
                    )
                )
            }
            else -> {
                ResultType.Fail(
                    it.changeData(null)
                )
            }
        }
    }

    override fun getTotalRecord(userSeq: Long): Flow<TotalRecordTypeResponse>
        = userRemoteDataSource.getTotalRecord(userSeq).asResultOtherType {
            when(it.code) {
                ResponseCodeStatus.USER_REQUEST_SUCCESS.code -> {
                    ResultType.Success(
                        it.changeData(it.data.mapperToTotalRecord())
                    )
                }

                else -> {
                    ResultType.Fail(
                        it.changeData(null)
                    )
                }
            }
    }

    override fun editProfile(userSeq: Long, profile: Profile): Flow<UserTypeResponse>
        = userRemoteDataSource.editProfile(userSeq, profile.mapperToEditProfileRequest()).asResultOtherType{
        when(it.code){
            ResponseCodeStatus.USER_REQUEST_SUCCESS.code -> {
                ResultType.Success(it.changeData(it.data.mapperToUser()))
            }
            else -> {
                ResultType.Fail(it.changeData(null))
            }
        }
    }
}
