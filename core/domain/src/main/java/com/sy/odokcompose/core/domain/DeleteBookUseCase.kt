package com.sy.odokcompose.core.domain

import com.sy.odokcompose.core.data.local.datasource.BookLocalDataSource
import com.sy.odokcompose.core.data.repository.BookShelfRepository
import javax.inject.Inject

sealed class DeleteBookResult {
    object Success: DeleteBookResult()
    data class Error(val message: String) : DeleteBookResult()
}

class DeleteBookUseCase @Inject constructor(
    private val bookLocalRepository: BookShelfRepository
) {
    suspend operator fun invoke(itemId: Int): DeleteBookResult {
        return try {
            bookLocalRepository.deleteBookById(itemId)
            DeleteBookResult.Success
        } catch (e: Exception) {
            DeleteBookResult.Error(e.message ?: "책 삭제 중 오류가 발생했습니다.")
        }
    }

    suspend fun invokeMultiple(itemIds: Set<Int>): DeleteBookResult {
        return try {
            itemIds.forEach { itemId ->
                bookLocalRepository.deleteBookById(itemId)
            }
            DeleteBookResult.Success
        } catch (e: Exception) {
            DeleteBookResult.Error(e.message ?: "책 삭제 중 오류가 발생했습니다.")
        }
    }
}