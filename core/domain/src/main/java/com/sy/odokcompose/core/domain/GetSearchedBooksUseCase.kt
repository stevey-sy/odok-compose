package com.sy.odokcompose.core.domain

import com.sy.odokcompose.core.data.repository.SearchBookRepository
import com.sy.odokcompose.model.SearchBookUiModel
import javax.inject.Inject

class GetSearchedBooksUseCase @Inject constructor(
    private val searchBookRepository: SearchBookRepository
) {
    suspend operator fun invoke(query: String, page: Int, pageSize: Int): List<SearchBookUiModel> {
        return searchBookRepository.searchBooks(query, page, pageSize)
    }
}