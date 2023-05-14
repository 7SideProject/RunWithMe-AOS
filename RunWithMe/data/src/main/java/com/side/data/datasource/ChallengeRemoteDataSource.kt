package com.side.data.datasource

import com.side.data.api.ChallengeApi
import com.side.data.model.response.ChallengeResponse
import com.side.domain.base.BaseResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeRemoteDataSource @Inject constructor(
    private val challengeApi: ChallengeApi
) {
    fun getChallengeList(): Flow<BaseResponse<List<ChallengeResponse>>> = flow {
        emit(challengeApi.getChallengeList())
    }
}