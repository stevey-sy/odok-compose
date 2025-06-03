package com.sy.odokcompose.core.domain

import com.sy.odokcompose.core.data.local.datasource.BookLocalDataSource
import com.sy.odokcompose.core.database.entity.BookEntity
import javax.inject.Inject

sealed class SaveBookResult {
    object Success : SaveBookResult()
    object DuplicateBook : SaveBookResult()
    data class Error(val message: String) : SaveBookResult()
}

class SaveBookUseCase @Inject constructor(
    private val bookLocalDataSource: BookLocalDataSource
) {
    suspend operator fun invoke(book: BookEntity): SaveBookResult {
        return try {
            // ISBN으로 기존 책이 있는지 확인
            val existingBook = bookLocalDataSource.getBookByIsbn(book.isbn)
            if (existingBook != null) {
                return SaveBookResult.DuplicateBook
            }
            
            // 중복이 없으면 저장
            bookLocalDataSource.insert(book)
            SaveBookResult.Success
        } catch (e: Exception) {
            SaveBookResult.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
        }
    }
}