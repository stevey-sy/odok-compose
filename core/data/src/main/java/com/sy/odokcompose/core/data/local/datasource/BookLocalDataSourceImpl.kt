package com.sy.odokcompose.core.data.local.datasource

import com.sy.odokcompose.core.database.BookDao
import com.sy.odokcompose.core.database.entity.BookEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BookLocalDataSourceImpl @Inject constructor(
    private val bookDao: BookDao
) : BookLocalDataSource {
    override fun getReadingBooks(): Flow<List<BookEntity>> {
        TODO("Not yet implemented")
    }

    override fun getFinishedBooks(): Flow<List<BookEntity>> {
        TODO("Not yet implemented")
    }

    override fun getAllBooks(): Flow<List<BookEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateReadingProgress(
        itemId: Int,
        page: Int,
        elapsedTime: Int
    ): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun insert(item: BookEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun insertAll(items: List<BookEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun update(item: BookEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(item: BookEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll() {
        TODO("Not yet implemented")
    }

    override fun observeAll(): Flow<List<BookEntity>> {
        TODO("Not yet implemented")
    }

    override fun observeById(id: String): Flow<BookEntity?> {
        TODO("Not yet implemented")
    }
}