package com.side.domain.repository

import androidx.paging.PagingData
import com.side.domain.base.BaseResponse
import com.side.domain.model.Challenge
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Part

typealias JoinResponse = ResultType<BaseResponse<Any?>>

interface ChallengeRepository {

    fun getChallengeList(
        size: Int,

    ): Flow<PagingData<Challenge>>

    fun createChallenge(challenge: Challenge, imgFile: MultipartBody.Part?): Flow<JoinResponse>

}