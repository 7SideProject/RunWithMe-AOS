package com.side.domain.repository

import androidx.paging.PagingData
import com.side.domain.base.BaseResponse
import com.side.domain.model.Challenge
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow



interface ChallengeRepository {

    fun getChallengeList(
        size: Int,

    ): Flow<PagingData<Challenge>>
}