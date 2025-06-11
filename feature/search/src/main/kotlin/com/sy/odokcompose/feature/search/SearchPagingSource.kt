package com.sy.odokcompose.feature.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sy.odokcompose.core.domain.GetSearchedBooksUseCase
import com.sy.odokcompose.model.SearchBookUiModel
import javax.inject.Inject

class SearchPagingSource @Inject constructor(
    private val getSearchedBooksUseCase: GetSearchedBooksUseCase,
    private val query: String
) : PagingSource<Int, SearchBookUiModel>() {

    override fun getRefreshKey(state: PagingState<Int, SearchBookUiModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchBookUiModel> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize
            
            val books = getSearchedBooksUseCase(query, page, pageSize)
            
            LoadResult.Page(
                data = books,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (books.size < pageSize) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
} 