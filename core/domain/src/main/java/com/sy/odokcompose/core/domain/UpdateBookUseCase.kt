package com.sy.odokcompose.core.domain

import com.sy.odokcompose.core.data.local.datasource.BookLocalDataSource
import com.sy.odokcompose.core.data.repository.BookShelfRepository
import com.sy.odokcompose.core.database.entity.BookEntity
import com.sy.odokcompose.core.database.entity.mapper.BookEntityMapper
import com.sy.odokcompose.model.BookUiModel
import javax.inject.Inject

sealed class UpdateBookResult {
    object Success: UpdateBookResult()
    data class Error(val message: String): UpdateBookResult()
}

class UpdateBookUseCase @Inject constructor(
    private val bookLocalRepository: BookShelfRepository
) {
    suspend operator fun invoke(bookUiModel: BookUiModel): UpdateBookResult {
            return try {
                val bookEntity = BookEntityMapper.modelToEntity(bookUiModel)
                bookLocalRepository.updateBook(bookEntity)
                UpdateBookResult.Success
            } catch (e: Exception) {
                UpdateBookResult.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
            }
    }
}