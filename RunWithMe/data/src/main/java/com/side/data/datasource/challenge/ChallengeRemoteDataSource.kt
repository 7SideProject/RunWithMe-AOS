package com.side.data.datasource.challenge

import com.side.data.model.request.ChallengeRequest
import com.side.domain.base.BaseResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Part

interface ChallengeRemoteDataSource {

    fun createChallenge(challenge: ChallengeRequest, imgFile: MultipartBody.Part?): Flow<BaseResponse<Any?>>

}