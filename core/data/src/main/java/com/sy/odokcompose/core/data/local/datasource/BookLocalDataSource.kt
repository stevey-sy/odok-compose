package com.sy.odokcompose.core.data.local.datasource

import com.sy.odokcompose.core.database.entity.BookEntity
import com.sy.odokcompose.model.type.ShelfFilterType
import kotlinx.coroutines.flow.Flow

interface BookLocalDataSource : LocalDataSource<BookEntity> {
    fun getBooksByFilterType(filterType: ShelfFilterType): Flow<List<BookEntity>>
    suspend fun updateReadingProgress(itemId: Int, page: Int, elapsedTime: Int): Boolean
    suspend fun getBookByIsbn(isbn: String): BookEntity?
    suspend fun deleteBookById(itemId: Int)
}