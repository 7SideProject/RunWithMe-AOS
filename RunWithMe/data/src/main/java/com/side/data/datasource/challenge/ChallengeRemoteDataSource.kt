package com.side.data.datasource.challenge

import com.side.data.model.response.ChallengeDetailResponse
import com.side.domain.base.BaseResponse
import com.side.domain.model.Challenge
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ChallengeRemoteDataSource {

    fun createChallenge(challengeRequestBody: RequestBody, imgFile: MultipartBody.Part?): Flow<BaseResponse<Any?>>

    fun getRecruitingChallengeList(
        cursor:Long, size: Int
    ): Flow<BaseResponse<List<Challenge>>>

    fun isChallengeAlreadyJoin(challengeSeq: Long): Flow<BaseResponse<String>>

    fun getMyChallengeList(cursorSeq: Long, size: Int): Flow<BaseResponse<List<Challenge>>>

    fun getAvailableRunningList(cursorSeq: Long, size: Int): Flow<BaseResponse<List<Challenge>>>

    fun joinChallenge(challengeSeq: Long, password: String?): Flow<BaseResponse<String?>>

    fun leaveChallenge(challengeSeq: Long): Flow<BaseResponse<Any?>>

    fun getChallengeDetail(challengeSeq: Long): Flow<BaseResponse<ChallengeDetailResponse>>
}