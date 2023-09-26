package com.side.data.datasource.challenge

import com.side.domain.base.BaseResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ChallengeRemoteDataSource {

    fun createChallenge(challengeRequestBody: RequestBody, imgFile: MultipartBody.Part?): Flow<BaseResponse<Any?>>

}