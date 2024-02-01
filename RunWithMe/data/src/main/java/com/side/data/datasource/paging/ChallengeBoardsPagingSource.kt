package com.side.data.datasource.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.side.data.datasource.challenge.ChallengeRemoteDataSource
import com.side.data.mapper.mapperToBoardList
import com.side.domain.model.Board
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeBoardsPagingSource @Inject constructor(
    private val challengeSeq: Long,
    private val size: Int,
    private val challengeRemoteDataSource: ChallengeRemoteDataSource
): PagingSource<Long, Board>(){

    override fun getRefreshKey(state: PagingState<Long, Board>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Board> = try {
        val nextCursor = params.key ?: 0L

        val response = challengeRemoteDataSource.getChallengeBoards(challengeSeq, nextCursor, size)

        val boardsList = response.first().data.mapperToBoardList()

        val nextKey = if(boardsList.size < size){
            null
        } else {
            boardsList.last().boardSeq
        }

        LoadResult.Page(
            data = boardsList,
            prevKey = null,
            nextKey = nextKey
        )

    } catch (exception: Exception){
        Firebase.crashlytics.recordException(exception)
        LoadResult.Error(exception)
    }
}