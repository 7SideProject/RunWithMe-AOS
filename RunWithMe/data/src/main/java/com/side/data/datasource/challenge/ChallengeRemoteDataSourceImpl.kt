package com.side.data.datasource.challenge

import com.side.data.api.ChallengeApi
import com.side.domain.base.BaseResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeRemoteDataSourceImpl @Inject constructor(
    private val challengeApi: ChallengeApi
) : ChallengeRemoteDataSource {

    override fun createChallenge(
        challengeRequestBody: RequestBody,
        imgFile: MultipartBody.Part?
    ): Flow<BaseResponse<Any?>> = flow {
        emit(challengeApi.createChallenge(challengeRequestBody, imgFile))
    }
}