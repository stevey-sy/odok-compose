package com.sy.odokcompose.core.domain

import com.sy.odokcompose.core.data.repository.SearchBookRepository
import com.sy.odokcompose.model.SearchBookUiModel
import javax.inject.Inject

class GetSearchBookDetailUseCase @Inject constructor(
    private val searchBookRepository: SearchBookRepository
) {
    suspend operator fun invoke(isbn: String): SearchBookUiModel {
        return searchBookRepository.getBookDetail(isbn)
    }
}