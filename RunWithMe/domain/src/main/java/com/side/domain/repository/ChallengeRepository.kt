package com.side.domain.repository

import androidx.paging.PagingData
import com.side.domain.base.BaseResponse
import com.side.domain.model.Challenge
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

typealias JoinResponse = ResultType<BaseResponse<Any?>>
typealias PagingChallengeResponse = ResultType<PagingData<Challenge>>

interface ChallengeRepository {

    fun getRecruitingChallengeList(
        size: Int
    ): Flow<PagingChallengeResponse>

    fun createChallenge(challenge: Challenge, imgFile: MultipartBody.Part?): Flow<JoinResponse>

    fun isChallengeAlreadyJoin(challengeSeq: Long): Flow<ResultType<BaseResponse<Boolean>>>

    fun getMyChallengeList(
        size: Int
    ): Flow<PagingChallengeResponse>
}