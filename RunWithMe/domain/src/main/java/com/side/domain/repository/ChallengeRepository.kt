package com.side.domain.repository

import com.side.domain.base.BaseResponse
import com.side.domain.model.Challenge
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow

typealias ChallengeListResponse = ResultType<BaseResponse<List<Challenge>>>

interface ChallengeRepository {

    fun getChallengeList(
        page: Int,
        size: Int,
        sort: Array<String>
    ): Flow<ChallengeListResponse>
}