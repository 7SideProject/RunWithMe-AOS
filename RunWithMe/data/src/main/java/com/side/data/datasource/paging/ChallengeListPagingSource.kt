package com.side.data.datasource.paging


import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.side.data.api.ChallengeApi
import com.side.data.mapper.mapperToChallenge
import com.side.data.model.response.ChallengeResponse
import com.side.domain.model.Challenge
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ChallengeListPagingSource @Inject constructor(
    private val size: Int,
    private val challengeApi: ChallengeApi
) : PagingSource<Int, Challenge>() {
    override fun getRefreshKey(state: PagingState<Int, Challenge>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Challenge> {
        return try {
            Log.d("test123", "ChallengeListPagingSource: ")
            val nextCursor = params.key ?: 0

            val response = challengeApi.getChallengeList(nextCursor, size)

            Log.d("test123", "load: ")
            val nextKey = if (response.data.size < size) {
                null
            } else {
                response.data.last().seq.toInt()
            }

            LoadResult.Page(
                data = response.data.map { it.mapperToChallenge() },
                prevKey = null,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            Log.d("test123", "IOException: $exception")
            LoadResult.Error(exception)

        } catch (exception: HttpException) {
            Log.d("test123", "HttpException: ${exception.message}")
            Log.d("test123", "HttpException: ${exception.localizedMessage}")
            Log.d("test123", "HttpException: ${exception.stackTrace.contentToString()}")
            Log.d("test123", "HttpException: ${exception.cause}")
            LoadResult.Error(exception)
        }
    }

}