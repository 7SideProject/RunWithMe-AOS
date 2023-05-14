package com.side.data.repository

import com.side.data.datasource.ChallengeRemoteDataSource
import com.side.data.mapper.mapperToChallenge
import com.side.domain.base.BaseResponse
import com.side.domain.repository.ChallengeListResponse
import com.side.domain.repository.ChallengeRepository
import com.side.domain.utils.ResultType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeRepositoryImpl @Inject constructor(
    private val challengeRemoteDataSource: ChallengeRemoteDataSource
) : ChallengeRepository {
    override fun getChallengeList(): Flow<ChallengeListResponse> = flow {
        emit(ResultType.Loading)
        challengeRemoteDataSource.getChallengeList().collectLatest {
            emit(ResultType.Success(
                BaseResponse(
                    it.message,
                    it.data.map { challengeResponse ->
                        challengeResponse.mapperToChallenge()
                    }
                )
            ))
        }

    }.catch {
        emit(
            ResultType.Error(
                it
            )
        )
    }.shareIn(
        CoroutineScope(Dispatchers.IO),
        replay = 1,
        started = SharingStarted.WhileSubscribed(5000)
    )
}