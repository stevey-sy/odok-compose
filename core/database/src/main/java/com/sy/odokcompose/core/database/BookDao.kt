package com.sy.odokcompose.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sy.odokcompose.core.database.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBook(book: BookEntity): Long
    
    @Update
    suspend fun updateBook(book: BookEntity)
    
    @Delete
    suspend fun deleteBook(book: BookEntity)
    
    @Query("DELETE FROM books WHERE itemId = :itemId")
    suspend fun deleteBookById(itemId: Int)
    
    @Query("SELECT * FROM books WHERE itemId = :itemId")
    fun getBookById(itemId: Int): Flow<BookEntity?>
    
    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    fun searchBooks(query: String): Flow<List<BookEntity>>

    @Query("SELECT * FROM books ORDER BY itemId DESC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE currentPageCnt < totalPageCnt ORDER BY itemId DESC")
    fun getReadingBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE currentPageCnt = totalPageCnt OR finishedReadCnt > 0 ORDER BY itemId DESC")
    fun getFinishedBooks(): Flow<List<BookEntity>>

    @Query("""
        UPDATE books 
        SET currentPageCnt = :currentPage, 
            elapsedTimeInSeconds = elapsedTimeInSeconds + :elapsedTime 
        WHERE itemId = :itemId
    """)
    suspend fun updateReadingProgress(itemId: Int, currentPage: Int, elapsedTime: Int): Int

    @Query("SELECT * FROM books WHERE isbn = :isbn LIMIT 1")
    suspend fun getBookByIsbn(isbn: String): BookEntity?

} 