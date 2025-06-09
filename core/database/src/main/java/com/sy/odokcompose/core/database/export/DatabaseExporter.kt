package com.sy.odokcompose.core.database.export

import android.content.Context
import android.util.Log
import com.sy.odokcompose.core.database.BookDao
import com.sy.odokcompose.core.database.MemoDao
import kotlinx.coroutines.flow.first
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseExporter @Inject constructor(
    private val context: Context,
    private val bookDao: BookDao,
    private val memoDao: MemoDao,
    private val bookJsonExporter: BookJsonExporter,
    private val memoJsonExporter: MemoJsonExporter
) {
    suspend fun exportDatabase(): Result<File> {
        return try {
            Log.d("DatabaseExporter", "Starting export process...")
            val books = bookDao.getAllBooks().first()
            val memos = memoDao.getAllMemos().first()
            Log.d("DatabaseExporter", "Retrieved ${books.size} books and ${memos.size} memos from database")
            
            val exportDir = File(context.filesDir, "exports")
            val dirCreated = exportDir.mkdirs()
            Log.d("DatabaseExporter", "Export directory created: $dirCreated at ${exportDir.absolutePath}")
            
            val timestamp = System.currentTimeMillis()
            val booksFile = File(exportDir, "books_$timestamp.json")
            val memosFile = File(exportDir, "memos_$timestamp.json")
            
            bookJsonExporter.exportBooks(books, booksFile)
            memoJsonExporter.exportMemos(memos, memosFile)
            
            // 파일 생성 여부 확인
            if (booksFile.exists() && memosFile.exists()) {
                Log.d("DatabaseExporter", "Files exist: ${booksFile.absolutePath}, ${memosFile.absolutePath}")
                Log.d("DatabaseExporter", "Books file size: ${booksFile.length()} bytes")
                Log.d("DatabaseExporter", "Memos file size: ${memosFile.length()} bytes")
            } else {
                Log.e("DatabaseExporter", "Some files do not exist")
            }
            
            Log.d("DatabaseExporter", "Export completed successfully")
            Result.success(exportDir)
        } catch (e: Exception) {
            Log.e("DatabaseExporter", "Export failed", e)
            Result.failure(e)
        }
    }
    
    suspend fun importDatabase(booksFile: File, memosFile: File): Result<Unit> {
        return try {
            val books = bookJsonExporter.importBooks(booksFile).getOrThrow()
            val memos = memoJsonExporter.importMemos(memosFile).getOrThrow()
            
            books.forEach { book ->
                bookDao.insertBook(book)
            }
            memos.forEach { memo ->
                memoDao.insertMemo(memo)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("DatabaseExporter", "Import failed", e)
            Result.failure(e)
        }
    }

    suspend fun importDummyData(): Result<Unit> {
        return try {
            Log.d("DatabaseExporter", "Starting dummy data import...")
            
            // 기존 데이터 삭제
            val existingBooks = bookDao.getAllBooks().first()
            existingBooks.forEach { book ->
                bookDao.deleteBook(book)
            }
            Log.d("DatabaseExporter", "Deleted ${existingBooks.size} existing books")
            
            // assets에서 더미 데이터 파일 읽기
            val booksJson = context.assets.open("books_dummy.json").bufferedReader().use { it.readText() }
            val memosJson = context.assets.open("memos_dummy.json").bufferedReader().use { it.readText() }
            Log.d("DatabaseExporter", "Read books_dummy.json: ${booksJson.length} bytes")
            Log.d("DatabaseExporter", "Read memos_dummy.json: ${memosJson.length} bytes")
            
            // 임시 파일에 저장
            val tempBooksFile = File(context.filesDir, "temp_books_dummy.json")
            val tempMemosFile = File(context.filesDir, "temp_memos_dummy.json")
            tempBooksFile.writeText(booksJson)
            tempMemosFile.writeText(memosJson)
            
            // 데이터 임포트
            val books = bookJsonExporter.importBooks(tempBooksFile).getOrThrow()
            val memos = memoJsonExporter.importMemos(tempMemosFile).getOrThrow()
            Log.d("DatabaseExporter", "Imported ${books.size} books")
            Log.d("DatabaseExporter", "Imported ${memos.size} memos")
            
            // 데이터베이스에 저장
            books.forEach { book ->
                bookDao.insertBook(book)
            }
            
            memos.forEach { memo ->
                memoDao.insertMemo(memo)
            }
            
            // 임시 파일 삭제
            tempBooksFile.delete()
            tempMemosFile.delete()
            
            // 저장된 데이터 확인
            val savedBooks = bookDao.getAllBooks().first()
            val savedMemos = memoDao.getAllMemos().first()
            Log.d("DatabaseExporter", "Successfully saved ${savedBooks.size} books")
            Log.d("DatabaseExporter", "Successfully saved ${savedMemos.size} memos")
            
            Log.d("DatabaseExporter", "Dummy data import completed successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("DatabaseExporter", "Failed to import dummy data", e)
            Result.failure(e)
        }
    }
} 