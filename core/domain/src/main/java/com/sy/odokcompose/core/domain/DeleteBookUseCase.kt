package com.sy.odokcompose.core.domain

import com.sy.odokcompose.core.data.local.datasource.BookLocalDataSource
import javax.inject.Inject

sealed class DeleteBookResult {
    object Success: DeleteBookResult()
    data class Error(val message: String) : DeleteBookResult()
}

class DeleteBookUseCase @Inject constructor(
    private val bookLocalDataSource: BookLocalDataSource
) {
    suspend operator fun invoke(itemId: Int): DeleteBookResult {
        return try {
            bookLocalDataSource.deleteBookById(itemId)
            DeleteBookResult.Success
        } catch (e: Exception) {
            DeleteBookResult.Error(e.message ?: "책 삭제 중 오류가 발생했습니다.")
        }
    }

    suspend fun invokeMultiple(itemIds: Set<Int>): DeleteBookResult {
        return try {
            itemIds.forEach { itemId ->
                bookLocalDataSource.deleteBookById(itemId)
            }
            DeleteBookResult.Success
        } catch (e: Exception) {
            DeleteBookResult.Error(e.message ?: "책 삭제 중 오류가 발생했습니다.")
        }
    }
}