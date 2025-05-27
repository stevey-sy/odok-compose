package com.sy.odokcompose.core.domain

import com.sy.odokcompose.core.data.repository.BookShelfRepository

import com.sy.odokcompose.model.BookUiModel
import com.sy.odokcompose.model.type.ShelfFilterType
import com.sy.odokcompose.model.type.ShelfFilterType.NONE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMyBooksUseCase @Inject constructor(
    private val bookShelfRepository: BookShelfRepository
) {
    suspend operator fun invoke(filterBy: ShelfFilterType = NONE): Flow<List<BookUiModel>> {
        return bookShelfRepository.getShelfItems(filterBy)
    }
}