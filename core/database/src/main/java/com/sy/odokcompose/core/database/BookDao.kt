package com.sy.odokcompose.core.database

import androidx.room.*
import com.sy.odokcompose.core.database.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    
    // 기본 CRUD 작업 (UUID 기반)
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBook(book: BookEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBooks(books: List<BookEntity>): List<Long>
    
    @Update
    suspend fun updateBook(book: BookEntity)
    
    @Update
    suspend fun updateBooks(books: List<BookEntity>)
    
    @Delete
    suspend fun deleteBook(book: BookEntity)
    
    @Query("DELETE FROM books WHERE itemId = :itemId")
    suspend fun deleteBookById(itemId: String) // UUID로 변경
    
    @Query("DELETE FROM books WHERE userId = :userId")
    suspend fun deleteBooksByUser(userId: String)
    
    // Upsert (Insert or Update)
    @Transaction
    suspend fun upsertBook(book: BookEntity) {
        val existingBook = getBookByIdSync(book.itemId)
        if (existingBook == null) {
            insertBook(book)
        } else {
            updateBook(book)
        }
    }
    
    @Transaction
    suspend fun upsertBooks(books: List<BookEntity>) {
        books.forEach { book ->
            upsertBook(book)
        }
    }
    
    // 조회 작업 (UUID 기반)
    
    @Query("SELECT * FROM books WHERE itemId = :itemId")
    suspend fun getBookByIdSync(itemId: String): BookEntity? // 동기 버전
    
    @Query("SELECT * FROM books WHERE itemId = :itemId")
    fun getBookById(itemId: String): Flow<BookEntity?> // UUID로 변경
    
    @Query("SELECT * FROM books WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllBooks(userId: String): Flow<List<BookEntity>>
    
    @Query("SELECT * FROM books WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getAllBooksSync(userId: String): List<BookEntity>
    
    @Query("SELECT * FROM books WHERE isbn = :isbn AND userId = :userId LIMIT 1")
    suspend fun getBookByIsbn(isbn: String, userId: String): BookEntity?
    
    // 독서 상태별 조회
    
    @Query("""
        SELECT * FROM books 
        WHERE userId = :userId 
        AND currentPageCnt < totalPageCnt 
        AND currentPageCnt > 0
        ORDER BY updatedAt DESC
    """)
    fun getReadingBooks(userId: String): Flow<List<BookEntity>>
    
    @Query("""
        SELECT * FROM books 
        WHERE userId = :userId 
        AND (currentPageCnt = totalPageCnt OR finishedReadCnt > 0)
        ORDER BY updatedAt DESC
    """)
    fun getFinishedBooks(userId: String): Flow<List<BookEntity>>
    
    @Query("""
        SELECT * FROM books 
        WHERE userId = :userId 
        AND currentPageCnt = 0
        ORDER BY createdAt DESC
    """)
    fun getNotStartedBooks(userId: String): Flow<List<BookEntity>>
    
    // 검색
    
    @Query("""
        SELECT * FROM books 
        WHERE userId = :userId 
        AND (title LIKE '%' || :query || '%' 
             OR author LIKE '%' || :query || '%'
             OR publisher LIKE '%' || :query || '%'
             OR description LIKE '%' || :query || '%')
        ORDER BY updatedAt DESC
    """)
    fun searchBooks(query: String, userId: String): Flow<List<BookEntity>>
    
    @Query("""
        SELECT * FROM books 
        WHERE userId = :userId 
        AND author = :author
        ORDER BY createdAt DESC
    """)
    fun getBooksByAuthor(author: String, userId: String): Flow<List<BookEntity>>
    
    @Query("""
        SELECT * FROM books 
        WHERE userId = :userId 
        AND publisher = :publisher
        ORDER BY createdAt DESC  
    """)
    fun getBooksByPublisher(publisher: String, userId: String): Flow<List<BookEntity>>
    
    // 독서 진행률 업데이트
    
    @Query("""
        UPDATE books 
        SET currentPageCnt = :currentPage, 
            updatedAt = :timestamp
        WHERE itemId = :itemId
    """)
    suspend fun updateCurrentPage(
        itemId: String, 
        currentPage: Int, 
        timestamp: Long = System.currentTimeMillis()
    ): Int
    
    @Query("""
        UPDATE books 
        SET elapsedTimeInSeconds = elapsedTimeInSeconds + :additionalTime,
            updatedAt = :timestamp
        WHERE itemId = :itemId
    """)
    suspend fun addReadingTime(
        itemId: String, 
        additionalTime: Int, 
        timestamp: Long = System.currentTimeMillis()
    ): Int
    
    @Query("""
        UPDATE books 
        SET currentPageCnt = :currentPage, 
            elapsedTimeInSeconds = elapsedTimeInSeconds + :elapsedTime,
            updatedAt = :timestamp
        WHERE itemId = :itemId
    """)
    suspend fun updateReadingProgress(
        itemId: String, 
        currentPage: Int, 
        elapsedTime: Int, 
        timestamp: Long = System.currentTimeMillis()
    ): Int
    
    @Query("""
        UPDATE books 
        SET currentPageCnt = totalPageCnt,
            finishedReadCnt = finishedReadCnt + 1,
            endDate = :endDate,
            updatedAt = :timestamp
        WHERE itemId = :itemId
    """)
    suspend fun markAsFinished(
        itemId: String, 
        endDate: String, 
        timestamp: Long = System.currentTimeMillis()
    ): Int
    
    @Query("""
        UPDATE books 
        SET rate = :rating,
            updatedAt = :timestamp
        WHERE itemId = :itemId
    """)
    suspend fun updateRating(
        itemId: String, 
        rating: Float, 
        timestamp: Long = System.currentTimeMillis()
    ): Int
    
    // 동기화 관련
    
    @Query("SELECT * FROM books WHERE userId = :userId AND updatedAt > :timestamp")
    suspend fun getBooksUpdatedSince(userId: String, timestamp: Long): List<BookEntity>
    
    @Query("SELECT MAX(updatedAt) FROM books WHERE userId = :userId")
    suspend fun getLastUpdateTime(userId: String): Long?
    
    @Query("SELECT COUNT(*) FROM books WHERE userId = :userId")
    suspend fun getBookCount(userId: String): Int
    
    // 통계
    
    @Query("SELECT COUNT(*) FROM books WHERE userId = :userId AND currentPageCnt = totalPageCnt")
    suspend fun getFinishedBookCount(userId: String): Int
    
    @Query("SELECT COUNT(*) FROM books WHERE userId = :userId AND currentPageCnt > 0 AND currentPageCnt < totalPageCnt")
    suspend fun getReadingBookCount(userId: String): Int
    
    @Query("SELECT SUM(elapsedTimeInSeconds) FROM books WHERE userId = :userId")
    suspend fun getTotalReadingTime(userId: String): Long?
    
    @Query("SELECT SUM(totalPageCnt) FROM books WHERE userId = :userId AND currentPageCnt = totalPageCnt")
    suspend fun getTotalPagesRead(userId: String): Long?
    
    @Query("SELECT AVG(rate) FROM books WHERE userId = :userId AND rate > 0")
    suspend fun getAverageRating(userId: String): Float?
    
    // 최근 활동
    
    @Query("""
        SELECT * FROM books 
        WHERE userId = :userId 
        ORDER BY updatedAt DESC 
        LIMIT :limit
    """)
    fun getRecentlyUpdatedBooks(userId: String, limit: Int = 10): Flow<List<BookEntity>>
    
    @Query("""
        SELECT * FROM books 
        WHERE userId = :userId 
        ORDER BY createdAt DESC 
        LIMIT :limit
    """)
    fun getRecentlyAddedBooks(userId: String, limit: Int = 10): Flow<List<BookEntity>>
    
    // 개발/테스트용
    
    @Query("DELETE FROM books")
    suspend fun deleteAllBooks()
    
    @Query("SELECT * FROM books")
    suspend fun getAllBooksForTesting(): List<BookEntity>
} 