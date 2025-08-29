package com.sy.odokcompose.core.data.local.datasource

import com.sy.odokcompose.core.database.BookDao
import com.sy.odokcompose.core.database.entity.BookEntity
import com.sy.odokcompose.core.supabase.auth.SupabaseAuthService
import com.sy.odokcompose.model.type.ShelfFilterType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class BookLocalDataSourceImpl @Inject constructor(
    private val bookDao: BookDao,
    private val authService: SupabaseAuthService
) : BookLocalDataSource {

    override fun getBooksByFilterType(filterType: ShelfFilterType): Flow<List<BookEntity>> {
        return authService.currentUser.flatMapLatest { user ->
            val userId = user?.id ?: return@flatMapLatest flowOf(emptyList())
            
            when (filterType) {
                ShelfFilterType.READING -> bookDao.getReadingBooks(userId)
                ShelfFilterType.FINISHED -> bookDao.getFinishedBooks(userId)
                ShelfFilterType.NONE -> bookDao.getAllBooks(userId)
                else -> bookDao.getAllBooks(userId)
            }
        }
    }

    override suspend fun updateReadingProgress(
        itemId: Int,
        page: Int,
        elapsedTime: Int
    ): Boolean {
        // itemId를 String으로 변환하여 BookDao에 전달
        return bookDao.updateReadingProgress(itemId.toString(), page, elapsedTime) > 0
    }

    override suspend fun insert(item: BookEntity) {
        bookDao.insertBook(item)
    }

    override suspend fun insertAll(items: List<BookEntity>) {
        bookDao.insertBooks(items)
    }

    override suspend fun update(item: BookEntity) {
        bookDao.updateBook(item)
    }

    override suspend fun delete(item: BookEntity) {
        bookDao.deleteBook(item)
    }

    override suspend fun deleteAll() {
        val currentUser = authService.getCurrentUser()
        val userId = currentUser?.id ?: return
        
        bookDao.deleteBooksByUser(userId)
    }

    override fun observeAll(): Flow<List<BookEntity>> {
        return authService.currentUser.flatMapLatest { user ->
            val userId = user?.id ?: return@flatMapLatest flowOf(emptyList())
            bookDao.getAllBooks(userId)
        }
    }

    override fun observeById(id: Int): Flow<BookEntity?> {
        // Int id를 String으로 변환하여 BookDao에 전달
        return bookDao.getBookById(id.toString())
    }

    override suspend fun getBookByIsbn(isbn: String): BookEntity? {
        val currentUser = authService.getCurrentUser()
        val userId = currentUser?.id ?: return null
        
        return bookDao.getBookByIsbn(isbn, userId)
    }

    override suspend fun deleteBookById(itemId: Int) {
        // Int itemId를 String으로 변환하여 BookDao에 전달
        return bookDao.deleteBookById(itemId.toString())
    }
}