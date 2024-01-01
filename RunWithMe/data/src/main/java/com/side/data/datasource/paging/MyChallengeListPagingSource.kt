package com.side.data.datasource.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.side.data.datasource.challenge.ChallengeRemoteDataSource
import com.side.data.mapper.mapperToChallengeList
import com.side.domain.model.Challenge
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyChallengeListPagingSource @Inject constructor(
    private val size: Int,
    private val challengeRemoteDataSource: ChallengeRemoteDataSource
) : PagingSource<Challenge, Challenge>() {

    override fun getRefreshKey(state: PagingState<Challenge, Challenge>): Challenge? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
                ?: state.closestPageToPosition(anchorPosition)?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<Challenge>): LoadResult<Challenge, Challenge> {
        return try {
            val nextCursor = params.key ?: Challenge(
                seq = 0,
                managerSeq = 0,
                managerName = "",
                name = "",
                description = "",
                image = 0,
                goalDays = 0,
                goalType = "",
                goalAmount = 0,
                dateStart = "",
                dateEnd = "",
                nowMember = 0,
                maxMember = 0,
                cost = 0,
                password = null
            )
            val response = challengeRemoteDataSource.getMyChallengeList(nextCursor.seq, size)

            val challengeList = response.first().data.mapperToChallengeList()

            val nextKey = if (challengeList.size < size) {
                null
            } else {
                challengeList.last()
            }

            LoadResult.Page(
                data = challengeList,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (exception: Exception) {
            Log.d("test123", "Exception: ${exception.message}")
            LoadResult.Error(exception)
        }
    }
}