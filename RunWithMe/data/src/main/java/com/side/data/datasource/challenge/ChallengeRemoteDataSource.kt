package com.side.data.datasource.challenge

import com.side.data.model.response.ChallengeBoardsResponse
import com.side.data.model.response.ChallengeDetailResponse
import com.side.data.model.response.ChallengeListResponse
import com.side.data.model.response.ChallengeRecordsResponse
import com.side.data.model.response.CreateBoardResponse
import com.side.domain.base.BaseResponse
import com.side.domain.model.Challenge
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ChallengeRemoteDataSource {

    fun createChallenge(challengeRequestBody: RequestBody, imgFile: MultipartBody.Part?): Flow<BaseResponse<Any?>>

    fun getRecruitingChallengeList(
        cursor:Long, dateStart: String, size: Int
    ): Flow<BaseResponse<List<ChallengeListResponse>>>

    fun isChallengeAlreadyJoin(challengeSeq: Long): Flow<BaseResponse<String>>

    fun getMyChallengeList(cursorSeq: Long, size: Int): Flow<BaseResponse<List<ChallengeListResponse>>>

    fun getAvailableRunningList(cursorSeq: Long, size: Int): Flow<BaseResponse<List<ChallengeListResponse>>>

    fun joinChallenge(challengeSeq: Long, password: String?): Flow<BaseResponse<String?>>

    fun leaveChallenge(challengeSeq: Long): Flow<BaseResponse<Any?>>

    fun getChallengeDetail(challengeSeq: Long): Flow<BaseResponse<ChallengeDetailResponse>>

    fun getRecordsList(challengeSeq: Long, size: Int): Flow<BaseResponse<List<ChallengeRecordsResponse>>>

    fun createBoard(challengeSeq: Long, content: RequestBody, image: MultipartBody.Part?): Flow<BaseResponse<CreateBoardResponse>>

    fun getChallengeBoards(challengeSeq: Long, cursorSeq: Long, size: Int): Flow<BaseResponse<List<ChallengeBoardsResponse>>>
}