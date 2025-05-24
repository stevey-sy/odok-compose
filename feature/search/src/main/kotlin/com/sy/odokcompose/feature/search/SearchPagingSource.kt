package com.sy.odokcompose.feature.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sy.odokcompose.core.data.repository.SearchBookRepository
import com.sy.odokcompose.model.SearchBookUiModel

class SearchPagingSource(
    private val searchBookRepository: SearchBookRepository,
    private val query: String
) : PagingSource<Int, SearchBookUiModel>() {

    override fun getRefreshKey(state: PagingState<Int, SearchBookUiModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchBookUiModel> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize
            
            val response = searchBookRepository.searchBooks(query, page, pageSize)
            
            LoadResult.Page(
                data = response,
                prevKey = if (page > 1) page - 1 else null,
                nextKey = if (response.size == pageSize) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
} 