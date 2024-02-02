package com.side.domain.repository

import androidx.paging.PagingData
import com.side.domain.base.BaseResponse
import com.side.domain.model.Board
import com.side.domain.model.Challenge
import com.side.domain.model.ChallengeRunRecord
import com.side.domain.model.TotalRecord
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

typealias JoinResponse = ResultType<BaseResponse<Any?>>
typealias ChallengeCreateResponse = ResultType<BaseResponse<Any?>>
typealias PagingChallengeResponse = Flow<PagingData<Challenge>>
typealias JoinChallengeResponse = ResultType<BaseResponse<String?>>
typealias IsChallengeJoinResponse = ResultType<BaseResponse<Boolean>>
typealias CreateBoardResponse = ResultType<BaseResponse<Boolean>>
typealias NullDataResponse = ResultType<BaseResponse<Any?>>

interface ChallengeRepository {

    fun getRecruitingChallengeList(
        size: Int
    ): Flow<PagingData<Challenge>>

    fun createChallenge(challenge: Challenge, imgFile: MultipartBody.Part?): Flow<ChallengeCreateResponse>

    fun isChallengeAlreadyJoin(challengeSeq: Long): Flow<IsChallengeJoinResponse>

    fun getMyChallengeList(
        size: Int
    ): Flow<PagingData<Challenge>>

    fun getAvailableRunningList(size: Int): Flow<PagingData<Challenge>>

    fun joinChallenge(
        challengeSeq: Long,
        password: String?
    ): Flow<JoinChallengeResponse>

    fun leaveChallenge(challengeSeq: Long): Flow<NullDataResponse>

    fun getRecordsList(size: Int): Flow<PagingData<ChallengeRunRecord>>


    fun createBoard(challengeSeq: Long, content: String, image: MultipartBody.Part?): Flow<CreateBoardResponse>

    fun getBoards(challengeSeq: Long, size: Int): Flow<PagingData<Board>>

    fun deleteBoard(boardSeq: Long): Flow<NullDataResponse>

    fun reportBoard(boardSeq: Long): Flow<NullDataResponse>

    fun getMyTotalRecordInChallenge(challengeSeq: Long): Flow<TotalRecordTypeResponse>

}