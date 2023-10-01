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
import javax.inject.Singleton

@Singleton
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

            val challengeList = response.data.result

            val nextKey = if (challengeList.size < size) {
                null
            } else {
                challengeList.last().seq.toInt()
            }
            Log.d("test123", "challengeList: $challengeList")
            LoadResult.Page(
                data = challengeList,
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
        } catch (exception: Exception) {
            Log.d("test123", "Exception: ${exception.message}")
            LoadResult.Error(exception)
        }
    }

}