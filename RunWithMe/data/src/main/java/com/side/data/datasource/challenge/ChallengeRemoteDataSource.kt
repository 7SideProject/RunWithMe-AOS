package com.side.data.datasource.challenge

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.side.data.datasource.paging.ChallengeListPagingSource
import com.side.domain.base.BaseResponse
import com.side.domain.model.Challenge
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Query

interface ChallengeRemoteDataSource {

    fun createChallenge(challengeRequestBody: RequestBody, imgFile: MultipartBody.Part?): Flow<BaseResponse<Any?>>

    fun getRecruitingChallengeList(
        cursor:Long, size: Int
    ): Flow<BaseResponse<List<Challenge>>>

    fun isChallengeAlreadyJoin(challengeSeq: Long): Flow<BaseResponse<String>>

    fun getMyChallengeList(cursorSeq: Long, size: Int): Flow<BaseResponse<List<Challenge>>>
}