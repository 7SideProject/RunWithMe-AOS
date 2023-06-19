package com.side.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.side.data.api.ChallengeApi
import com.side.data.datasource.ChallengeRemoteDataSource
import com.side.data.datasource.paging.ChallengeListPagingSource
import com.side.data.mapper.mapperToChallenge
import com.side.data.model.response.ChallengeResponse
import com.side.domain.model.Challenge
import com.side.domain.repository.ChallengeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeRepositoryImpl @Inject constructor(
    private val challengeRemoteDataSource: ChallengeRemoteDataSource,
    private val challengeApi: ChallengeApi
) : ChallengeRepository {
    override fun getChallengeList(
        size: Int
        ): Flow<PagingData<Challenge>> {
        Log.d("test123", "ChallengeRepositoryImpl: ")
        val pagingSourceFactory =
            { ChallengeListPagingSource(size, challengeApi = challengeApi) }
        return Pager(
            config = PagingConfig(
                pageSize = size,
                enablePlaceholders = false,
                maxSize = size * 3
            ), pagingSourceFactory = pagingSourceFactory
        ).flow

    }
}