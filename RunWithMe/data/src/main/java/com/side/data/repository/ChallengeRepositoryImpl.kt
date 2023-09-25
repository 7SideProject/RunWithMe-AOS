package com.side.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.side.data.api.ChallengeApi
import com.side.data.datasource.challenge.ChallengeRemoteDataSource
import com.side.data.datasource.paging.ChallengeListPagingSource
import com.side.data.mapper.mapperToChallengeRequest
import com.side.data.util.ResponseCodeStatus
import com.side.data.util.emitResultTypeError
import com.side.data.util.emitResultTypeFail
import com.side.data.util.emitResultTypeLoading
import com.side.data.util.emitResultTypeSuccess
import com.side.domain.base.BaseResponse
import com.side.domain.base.changeMessageAndData
import com.side.domain.model.Challenge
import com.side.domain.repository.ChallengeRepository
import com.side.domain.repository.JoinResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeRepositoryImpl @Inject constructor(
    private val challengeApi: ChallengeApi,
    private val challengeRemoteDataSource: ChallengeRemoteDataSource
) : ChallengeRepository {
    override fun getChallengeList(
        size: Int
        ): Flow<PagingData<Challenge>> {
        Log.d("test123", "ChallengeRepositoryImpl: ")
        val pagingSourceFactory =
            { ChallengeListPagingSource(size, challengeApi = challengeApi) }
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
    ): Flow<JoinResponse> = flow {
        emitResultTypeLoading()
        challengeRemoteDataSource.createChallenge(challenge.mapperToChallengeRequest(), imgFile).collect {

            when(it.code){

                ResponseCodeStatus.CREATE_CHALLENGE_SUCCESS.code -> {
                    emitResultTypeSuccess(
                        it.changeMessageAndData(
                            it.message,
                            it.data
                        )
                    )
                }

                /** 실패 다른 경우들 처리해야함**/
                ResponseCodeStatus.CREATE_CHALLENGE_FAIL.code -> {
                    emitResultTypeFail(it.changeMessageAndData(
                        it.message,
                        it.data
                    ))
                }
            }

        }
    }.catch {
        emitResultTypeError(it)
    }
}