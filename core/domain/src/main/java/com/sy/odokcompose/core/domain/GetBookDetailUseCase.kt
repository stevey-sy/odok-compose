package com.sy.odokcompose.core.domain

import com.sy.odokcompose.core.data.repository.BookShelfRepository
import com.sy.odokcompose.model.BookUiModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookDetailUseCase @Inject constructor(
    private val bookShelfRepository: BookShelfRepository
){
    suspend operator fun invoke(itemId : Int) : Flow<BookUiModel> {
        return bookShelfRepository.getBookById(itemId)
    }
}