package com.side.data.datasource.paging


import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.side.data.api.ChallengeApi
import com.side.data.model.response.ChallengeResponse
import com.side.domain.model.Challenge
import com.side.domain.usecase.user.GetChallengeListUseCase
import kotlinx.coroutines.flow.collectLatest
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ChallengeListPagingSource @Inject constructor(
    private val page: Int,
    private val size: Int,
    private val sort: Array<String>,
    private val challengeApi: ChallengeApi
) : PagingSource<Int, ChallengeResponse>() {
    override fun getRefreshKey(state: PagingState<Int, ChallengeResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChallengeResponse> {
        return try {

            val pageNumber = params.key ?: 0
            val response = challengeApi.getChallengeList(page, size, sort)

            /** 현재 페이지가 마지막 페이지라면 nextKey = null이고 아니면 nextKey는 현재 페이지 넘버에서 로드한 사이즈 / 페이징 할 사이즈를 더해준 값이 됩니다. **/
            val nextKey = if (response.data.size < size) {
                null
            } else {
                response.data.last().seq.toInt()
            }

            LoadResult.Page(
                data = response.data, prevKey = null, nextKey = nextKey
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

}