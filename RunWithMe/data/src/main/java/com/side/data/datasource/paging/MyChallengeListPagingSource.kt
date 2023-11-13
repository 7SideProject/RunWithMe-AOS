package com.side.data.datasource.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.side.data.datasource.challenge.ChallengeRemoteDataSource
import com.side.domain.model.Challenge
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyChallengeListPagingSource @Inject constructor(
    private val size: Int,
    private val challengeRemoteDataSource: ChallengeRemoteDataSource
) : PagingSource<Long, Challenge>() {

    override fun getRefreshKey(state: PagingState<Long, Challenge>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Challenge> {
        return try {
            val nextCursor = params.key ?: 0

            val response = challengeRemoteDataSource.getMyChallengeList(nextCursor, size)

            val challengeList = response.first().data

            val nextKey = if (challengeList.size < size) {
                null
            } else {
                challengeList.last().seq
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