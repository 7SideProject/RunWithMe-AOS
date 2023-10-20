package com.side.data.datasource.challenge

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.side.data.api.ChallengeApi
import com.side.data.datasource.paging.ChallengeListPagingSource
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
        cursor: Long, size: Int
    ): Flow<BaseResponse<List<Challenge>>> = flow {
        emit(challengeApi.getRecruitingChallengeList(cursor, size))
    }

    override fun isChallengeAlreadyJoin(challengeSeq: Long): Flow<BaseResponse<String>> = flow {
        emit(challengeApi.isChallengeAlreadyJoin(challengeSeq))
    }

    override fun getMyChallengeList(
        cursorSeq: Long,
        size: Int
    ): Flow<BaseResponse<List<Challenge>>> = flow {
        emit(challengeApi.getMyChallengeList(cursorSeq, size))
    }

    override fun joinChallenge(challengeSeq: Long, password: String?): Flow<BaseResponse<String?>> = flow {
        emit(challengeApi.joinChallenge(challengeSeq, password))
    }
}