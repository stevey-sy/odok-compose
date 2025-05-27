package com.sy.odokcompose.core.data.repository

import com.sy.odokcompose.core.data.local.datasource.BookLocalDataSource
import com.sy.odokcompose.model.BookUiModel
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * BookShelfRepository 인터페이스 구현체
 */
@Singleton
class BookShelfRepositoryImpl @Inject constructor(
    private val bookLocalDataSource: BookLocalDataSource
) : BookShelfRepository {
    override suspend fun getShelfItems(): Flow<List<BookUiModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun getBookById(itemId: Int): Flow<BookUiModel> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteBookById(itemId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun updateReadingProgress(
        itemId: Int,
        page: Int,
        elapsedTime: Int
    ) {
        TODO("Not yet implemented")
    }

}