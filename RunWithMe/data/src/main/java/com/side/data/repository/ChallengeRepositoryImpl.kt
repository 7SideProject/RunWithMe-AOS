package com.side.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.gson.Gson
import com.side.data.datasource.challenge.ChallengeRemoteDataSource
import com.side.data.datasource.paging.ChallengeListPagingSource
import com.side.data.datasource.paging.MyChallengeListPagingSource
import com.side.data.util.ResponseCodeStatus
import com.side.data.util.asResult
import com.side.data.util.asResultOtherType
import com.side.data.util.emitResultTypeError
import com.side.data.util.emitResultTypeFail
import com.side.data.util.emitResultTypeLoading
import com.side.data.util.emitResultTypeSuccess
import com.side.domain.model.Challenge
import com.side.domain.repository.ChallengeCreateResponse
import com.side.domain.repository.ChallengeRepository
import com.side.domain.repository.JoinResponse
import com.side.domain.repository.IsChallengeJoinResponse
import com.side.domain.repository.JoinChallengeResponse
import com.side.domain.repository.NullDataResponse
import com.side.domain.repository.PagingChallengeResponse
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeRepositoryImpl @Inject constructor(
    private val challengeRemoteDataSource: ChallengeRemoteDataSource
) : ChallengeRepository {

    override fun getRecruitingChallengeList(size: Int): Flow<PagingData<Challenge>> {

        val pagingSourceFactory =
            {
                ChallengeListPagingSource(
                    size,
                    challengeRemoteDataSource = challengeRemoteDataSource
                )
            }
        return Pager(
            config = PagingConfig(
                pageSize = size,
                enablePlaceholders = false,
                maxSize = size * 3
            ), pagingSourceFactory = pagingSourceFactory
        ).flow
    }


    override fun createChallenge(
        challenge: Challenge,
        imgFile: MultipartBody.Part?
    ): Flow<ChallengeCreateResponse> = flow {
        emitResultTypeLoading()

        val json = Gson().toJson(challenge)
        val challengeRequestBody =
            json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        challengeRemoteDataSource.createChallenge(challengeRequestBody, imgFile).collect {

            when (it.code) {

                ResponseCodeStatus.CREATE_CHALLENGE_SUCCESS.code -> {
                    emitResultTypeSuccess(
                        it.changeMessageAndData(
                            it.message,
                            it.data
                        )
                    )
                }
                else -> {
                    emitResultTypeFail(
                        it.changeMessage(
                            "잘못된 입력입니다."
                        )
                    )
                }
            }

        }
    }.catch {
        emitResultTypeError(it)
    }

    override fun isChallengeAlreadyJoin(challengeSeq: Long): Flow<IsChallengeJoinResponse> =
        challengeRemoteDataSource.isChallengeAlreadyJoin(challengeSeq).asResultOtherType {
            when (it.code) {
                ResponseCodeStatus.CHECK_IN_CHALLENGE_SUCCESS.code -> {
                    ResultType.Success(
                        it.changeMessageAndData(
                            ResponseCodeStatus.CHECK_IN_CHALLENGE_SUCCESS.message,
                            true
                        )
                    )
                }

                ResponseCodeStatus.CHECK_IN_CHALLENGE_FAIL.code -> {
                    ResultType.Success(
                        it.changeMessageAndData(
                            ResponseCodeStatus.CHECK_IN_CHALLENGE_FAIL.message,
                            false
                        )
                    )
                }

                else -> {
                    ResultType.Fail(
                        it.changeData(false)
                    )
                }
            }
        }

    override fun getMyChallengeList(size: Int): Flow<PagingData<Challenge>> {
        val pagingSourceFactory =
            {
                MyChallengeListPagingSource(
                    size,
                    challengeRemoteDataSource = challengeRemoteDataSource
                )
            }
        return Pager(
            config = PagingConfig(
                pageSize = size,
                enablePlaceholders = false,
                maxSize = size * 3
            ), pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override fun joinChallenge(challengeSeq: Long, password: String?): Flow<JoinChallengeResponse> =
        challengeRemoteDataSource.joinChallenge(challengeSeq, password).asResult {
            when(it.code) {
                ResponseCodeStatus.JOIN_CHALLENGE_SUCCESS.code -> {
                    ResultType.Success(
                        it.changeMessageAndData(
                            ResponseCodeStatus.JOIN_CHALLENGE_SUCCESS.message,
                            it.data
                        )
                    )
                }
                ResponseCodeStatus.JOIN_CHALLENGE_FAIL.code -> {
                    ResultType.Fail(
                        it.changeMessageAndData(
                            ResponseCodeStatus.JOIN_CHALLENGE_FAIL.message,
                            it.data
                        )
                    )
                }
                else -> {
                    ResultType.Fail(
                        it
                    )
                }
            }
        }

    override fun leaveChallenge(challengeSeq: Long): Flow<NullDataResponse> = challengeRemoteDataSource.leaveChallenge(challengeSeq).asResult {
        when(it.code){
            ResponseCodeStatus.DELETE_CHALLENGE_SUCCESS.code -> {
                ResultType.Success(
                    it
                )
            }

            ResponseCodeStatus.LEAVE_CHALLENGE_SUCCESS.code -> {
                ResultType.Success(it)
            }

            ResponseCodeStatus.CHALLENGE_NOT_FOUND.code -> {
                ResultType.Fail(it.changeMessage(ResponseCodeStatus.CHALLENGE_NOT_FOUND.message))
            }

            ResponseCodeStatus.CHALLENGE_IS_NOT_MY_CHALLENGE.code -> {
                ResultType.Fail(it.changeMessage(ResponseCodeStatus.CHALLENGE_IS_NOT_MY_CHALLENGE.message))
            }

            else -> {
                ResultType.Fail(it)
            }

        }
    }
}