package com.side.domain.repository

import androidx.paging.PagingData
import com.side.domain.base.BaseResponse
import com.side.domain.model.Challenge
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

typealias ChallengeCreateResponse = ResultType<BaseResponse<Any?>>
typealias PagingChallengeResponse = ResultType<PagingData<Challenge>>
typealias JoinChallengeResponse = ResultType<BaseResponse<String?>>

interface ChallengeRepository {

    fun getRecruitingChallengeList(
        size: Int
    ): Flow<PagingChallengeResponse>

    fun createChallenge(challenge: Challenge, imgFile: MultipartBody.Part?): Flow<ChallengeCreateResponse>

    fun isChallengeAlreadyJoin(challengeSeq: Long): Flow<ResultType<BaseResponse<Boolean>>>

    fun getMyChallengeList(
        size: Int
    ): Flow<PagingChallengeResponse>

    fun joinChallenge(
        challengeSeq: Long,
        password: String?
    ): Flow<JoinChallengeResponse>
}