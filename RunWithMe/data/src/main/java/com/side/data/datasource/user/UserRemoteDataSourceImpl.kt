package com.side.data.datasource.user

import com.side.data.api.LoginApi
import com.side.data.api.SocialJoinApi
import com.side.data.api.UserApi
import com.side.data.model.request.EditProfileRequest
import com.side.data.model.request.EmailLoginRequest
import com.side.data.model.request.FindPasswordRequest
import com.side.data.model.request.JoinRequest
import com.side.data.model.request.KakaoLoginRequest
import com.side.data.model.request.LoginRequest
import com.side.data.model.response.DailyCheckResponse
import com.side.data.model.response.DuplicateCheckResponse
import com.side.data.model.response.EmailLoginResponse
import com.side.data.model.response.KaKaoLoginResponse
import com.side.data.model.response.TotalRecordResponse
import com.side.data.model.response.UserResponse
import com.side.domain.base.BaseResponse
import com.side.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRemoteDataSourceImpl @Inject constructor(
    private val userApi: UserApi,
    private val loginApi: LoginApi,
    private val socialJoinApi: SocialJoinApi
): UserRemoteDataSource {
//    override fun login(loginRequest: LoginRequest): Flow<BaseResponse<User>> = flow {
//        emit(userApi.login(loginRequest.code, loginRequest.state))
//    }

    override fun join(joinRequest: JoinRequest): Flow<BaseResponse<UserResponse>> = flow {
        emit(userApi.join(joinRequest))
    }

    override fun loginWithEmail(emailLoginRequest: EmailLoginRequest): Flow<BaseResponse<EmailLoginResponse?>> =
        flow {
            emit(loginApi.loginWithEmail(emailLoginRequest))
        }

    override fun loginWithKakao(kakaoLoginRequest: KakaoLoginRequest): Flow<BaseResponse<KaKaoLoginResponse>> = flow {
        emit(loginApi.loginWithKakao(kakaoLoginRequest))
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

    override fun editProfile(
        userSeq: Long,
        editProfileRequest: EditProfileRequest
    ) = flow {
        emit(userApi.editProfile(userSeq, editProfileRequest))
    }

     override fun deleteUser(userSeq: Long): Flow<BaseResponse<Any?>> = flow {
        emit(userApi.deleteUser(userSeq))
    }

    override fun editProfileImage(userSeq: Long, image: MultipartBody.Part): Flow<BaseResponse<Any?>?> = flow {
        val response = userApi.editProfileImage(userSeq, image)
        if(response.isSuccessful && response.code() == 204){
            emit(null)
        }else{
            emit(response.body())
        }
    }

    override fun joinSocialUser(
        userSeq: Long,
        editProfileRequest: EditProfileRequest
    ): Flow<BaseResponse<UserResponse>> = flow {
        emit(socialJoinApi.joinSocialUser(userSeq, editProfileRequest))
    }
}