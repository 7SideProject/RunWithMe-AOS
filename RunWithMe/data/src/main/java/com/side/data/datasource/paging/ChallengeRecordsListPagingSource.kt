package com.side.data.datasource.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.side.data.datasource.challenge.ChallengeRemoteDataSource
import com.side.data.mapper.mapperToChallengeRunRecordList
import com.side.domain.model.ChallengeRunRecord
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeRecordsListPagingSource @Inject constructor(
    private val size: Int,
    private val challengeRemoteDataSource: ChallengeRemoteDataSource
): PagingSource<ChallengeRunRecord, ChallengeRunRecord>() {
    override fun getRefreshKey(state: PagingState<ChallengeRunRecord, ChallengeRunRecord>): ChallengeRunRecord? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
                ?: state.closestPageToPosition(anchorPosition)?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<ChallengeRunRecord>): LoadResult<ChallengeRunRecord, ChallengeRunRecord> = try {
        val nextCursor = params.key ?: ChallengeRunRecord(
            seq = 0,
            userSeq = 0,
            nickname = "",
            runningDay = "",
            startTime = "",
            runningDistance = 0,
            runningTime = 0,
            calorie = 0,
            avgSpeed = 0.0
        )

        val response = challengeRemoteDataSource.getRecordsList(nextCursor.seq, size)

        val challengeRecordList = response.first().data.mapperToChallengeRunRecordList()

        val nextKey = if (challengeRecordList.size < size){
            null
        } else {
            challengeRecordList.last()
        }

        LoadResult.Page(
            data = challengeRecordList,
            prevKey =  null,
            nextKey = nextKey
        )
    } catch (exception: Exception){
        LoadResult.Error(exception)
    }
}