package com.side.data.datasource.paging


import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.side.data.api.ChallengeApi
import com.side.data.datasource.challenge.ChallengeRemoteDataSource
import com.side.data.mapper.mapperToChallenge
import com.side.data.mapper.mapperToChallengeList
import com.side.data.model.response.ChallengeResponse
import com.side.domain.model.Challenge
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

//@Singleton
//class ChallengeListPagingSource @Inject constructor(
//    private val size: Int,
//    private val challengeRemoteDataSource: ChallengeRemoteDataSource
//) : PagingSource<Long, Challenge>() {
//    override fun getRefreshKey(state: PagingState<Long, Challenge>): Long? {
//        return state.anchorPosition?.let { anchorPosition ->
//            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
//                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
//        }
//    }
//
//    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Challenge> {
//        return try {
//            val nextCursor = params.key ?: 0
//
//            val response = challengeRemoteDataSource.getRecruitingChallengeList(nextCursor, size)
//
//            val challengeList = response.first().data
//
//            val nextKey = if (challengeList.size < size) {
//                null
//            } else {
//                challengeList.last().seq
//            }
//
//            LoadResult.Page(
//                data = challengeList,
//                prevKey = null,
//                nextKey = nextKey
//            )
//        } catch (exception: Exception) {
//            Log.d("test123", "Exception: ${exception.message}")
//            LoadResult.Error(exception)
//        }
//    }
//
//}

@Singleton
class ChallengeListPagingSource @Inject constructor(
    private val size: Int,
    private val challengeRemoteDataSource: ChallengeRemoteDataSource
) : PagingSource<Challenge, Challenge>() {

    companion object {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }

    override fun getRefreshKey(state: PagingState<Challenge, Challenge>): Challenge? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
                ?: state.closestPageToPosition(anchorPosition)?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<Challenge>): LoadResult<Challenge, Challenge> {
        return try {
            val today = LocalDateTime.now().format(formatter)

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
                dateStart = today,
                dateEnd = "",
                nowMember = 0,
                maxMember = 0,
                cost = 0,
                password = null
            )

            val response = challengeRemoteDataSource.getRecruitingChallengeList(nextCursor.seq, nextCursor.dateStart, size)

            val challengeList = response.first().data.mapperToChallengeList()

            val nextKey = if (challengeList.size < size) {
                null
            } else {
                challengeList.last()
            }

            val prevKey = if (challengeList.size != 0) {
                challengeList.first()
            } else {
                null
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