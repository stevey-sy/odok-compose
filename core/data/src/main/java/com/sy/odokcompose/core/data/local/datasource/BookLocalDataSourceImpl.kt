package com.sy.odokcompose.core.data.local.datasource

import com.sy.odokcompose.core.database.BookDao
import com.sy.odokcompose.core.database.entity.BookEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.compareTo

class BookLocalDataSourceImpl @Inject constructor(
    private val bookDao: BookDao
) : BookLocalDataSource {
    override fun getReadingBooks(): Flow<List<BookEntity>> {
        return bookDao.getReadingBooks()
    }

    override fun getFinishedBooks(): Flow<List<BookEntity>> {
        return bookDao.getFinishedBooks()
    }

    override fun getAllBooks(): Flow<List<BookEntity>> {
        return bookDao.getAllBooks()
    }

    override suspend fun updateReadingProgress(
        itemId: Int,
        page: Int,
        elapsedTime: Int
    ): Boolean {
        return bookDao.updateReadingProgress(itemId, page, elapsedTime) > 0
    }

    override suspend fun insert(item: BookEntity) {
        bookDao.insertBook(item)
    }

    override suspend fun insertAll(items: List<BookEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun update(item: BookEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(item: BookEntity) {
        bookDao.deleteBook(item)
    }

    override suspend fun deleteAll() {
        TODO("Not yet implemented")
    }

    override fun observeAll(): Flow<List<BookEntity>> {
        TODO("Not yet implemented")
    }

    override fun observeById(id: Int): Flow<BookEntity?> {
        return  bookDao.getBookById(id)
    }
}