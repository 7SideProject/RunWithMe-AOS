package com.side.data.datasource.challenge

import android.util.Log
import com.side.data.api.ChallengeApi
import com.side.data.model.response.ChallengeDetailResponse
import com.side.data.model.response.ChallengeListResponse
import com.side.data.model.response.ChallengeRecordsResponse
import com.side.data.model.response.CreateBoardResponse
import com.side.domain.base.BaseResponse
import com.side.domain.model.Challenge
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

    override fun getRecruitingChallengeList(
        cursor: Long, dateStart: String, size: Int
    ): Flow<BaseResponse<List<ChallengeListResponse>>> = flow {
        emit(challengeApi.getRecruitingChallengeList(cursor, dateStart, size))
    }

    override fun isChallengeAlreadyJoin(challengeSeq: Long): Flow<BaseResponse<String>> = flow {
        emit(challengeApi.isChallengeAlreadyJoin(challengeSeq))
    }

    override fun getMyChallengeList(
        cursorSeq: Long,
        size: Int
    ): Flow<BaseResponse<List<ChallengeListResponse>>> = flow {
        emit(challengeApi.getMyChallengeList(cursorSeq, size))
    }

    override fun getAvailableRunningList(
        cursorSeq: Long,
        size: Int
    ): Flow<BaseResponse<List<ChallengeListResponse>>> = flow {
        emit(challengeApi.getAvailableRunningList(cursorSeq, size))
    }

    override fun joinChallenge(challengeSeq: Long, password: String?): Flow<BaseResponse<String?>> = flow {
        emit(challengeApi.joinChallenge(challengeSeq, password))
    }

    override fun leaveChallenge(challengeSeq: Long): Flow<BaseResponse<Any?>> = flow {
        emit(challengeApi.leaveChallenge(challengeSeq))
    }

    override fun getChallengeDetail(challengeSeq: Long): Flow<BaseResponse<ChallengeDetailResponse>> = flow {
        emit(challengeApi.getChallengeDetail(challengeSeq))
    }

    override fun getRecordsList(challengeSeq: Long, size: Int): Flow<BaseResponse<List<ChallengeRecordsResponse>>> = flow {
        emit(challengeApi.getRecordsList(challengeSeq, size))
    }

    override fun createBoard(
        challengeSeq: Long,
        content: RequestBody,
        image: MultipartBody.Part?
    ): Flow<BaseResponse<CreateBoardResponse>> = flow {
        emit(challengeApi.createBoard(challengeSeq, content, image))
    }
}