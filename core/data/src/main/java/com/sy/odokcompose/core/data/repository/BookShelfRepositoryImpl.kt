package com.sy.odokcompose.core.data.repository

import com.sy.odokcompose.core.data.local.datasource.BookLocalDataSource
import com.sy.odokcompose.core.database.entity.mapper.BookEntityMapper
import com.sy.odokcompose.model.BookUiModel
import com.sy.odokcompose.model.type.ShelfFilterType
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * BookShelfRepository 인터페이스 구현체
 */

class BookShelfRepositoryImpl @Inject constructor(
    private val bookLocalDataSource: BookLocalDataSource
) : BookShelfRepository {
    override suspend fun getShelfItems(filterBy: ShelfFilterType): Flow<List<BookUiModel>> {
        return bookLocalDataSource.getBooksByFilterType(filterBy)
            .map { entityList ->
                entityList.map { BookEntityMapper.entityToModel(it) }
            }
    }

    override suspend fun getBookById(itemId: Int): Flow<BookUiModel> {
        return bookLocalDataSource.observeById(itemId).map { entity ->
            entity?.let { BookEntityMapper.entityToModel(it) }
                ?: throw IllegalStateException("Book with id $itemId not found")
        }
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