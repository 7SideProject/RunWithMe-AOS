package com.side.data.repository

import com.side.data.datasource.ChallengeRemoteDataSource
import com.side.data.mapper.mapperToChallenge
import com.side.domain.base.BaseResponse
import com.side.domain.repository.ChallengeListResponse
import com.side.domain.repository.ChallengeRepository
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeRepositoryImpl @Inject constructor(
    private val challengeRemoteDataSource: ChallengeRemoteDataSource
) : ChallengeRepository {
    //    override fun getChallengeList(): Flow<ChallengeListResponse> = flow {
//        emit(ResultType.Loading)
//        challengeRemoteDataSource.getChallengeList().collectLatest {
//            emit(ResultType.Success(
//                BaseResponse(
//                    it.message,
//                    it.data.map { challengeResponse ->
//                        challengeResponse.mapperToChallenge()
//                    }
//                )
//            ))
//        }
//
//    }.catch {
//        emit(
//            ResultType.Error(
//                it
//            )
//        )
//    }.shareIn(
//        CoroutineScope(Dispatchers.IO),
//        replay = 1,
//        started = SharingStarted.WhileSubscribed(5000)
//    )
    override fun getChallengeList(
        page: Int,
        size: Int,
        sort: Array<String>
    ): Flow<ChallengeListResponse> = flow {
        emit(ResultType.Loading)
        challengeRemoteDataSource.getChallengeList(page, size, sort).collectLatest {
            emit(ResultType.Success(
                BaseResponse(
                    it.code,
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
    }
}