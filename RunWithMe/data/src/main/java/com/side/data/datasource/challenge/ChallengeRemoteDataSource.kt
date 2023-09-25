package com.side.data.datasource.challenge

import com.side.data.model.request.CreateChallengeRequest
import com.side.domain.base.BaseResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface ChallengeRemoteDataSource {

    fun createChallenge(challenge: CreateChallengeRequest, imgFile: MultipartBody.Part?): Flow<BaseResponse<Any?>>

}