package com.sy.odokcompose.core.data.local.datasource

import com.sy.odokcompose.core.database.entity.BookEntity
import kotlinx.coroutines.flow.Flow

interface BookLocalDataSource : LocalDataSource<BookEntity> {
    fun getReadingBooks(): Flow<List<BookEntity>>
    fun getFinishedBooks(): Flow<List<BookEntity>>
    fun getAllBooks(): Flow<List<BookEntity>>
    suspend fun updateReadingProgress(itemId: Int, page: Int, elapsedTime: Int): Boolean
}