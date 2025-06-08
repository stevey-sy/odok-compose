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
            Result.failure(e)
        }
    }

    suspend fun importDummyData(): Result<Unit> {
        return try {
            val booksFile = File(context.filesDir, "books_dummy.json")
            val memosFile = File(context.filesDir, "memos_dummy.json")
            
            if (!booksFile.exists()) {
                context.assets.open("books_dummy.json").use { input ->
                    booksFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            
            if (!memosFile.exists()) {
                context.assets.open("memos_dummy.json").use { input ->
                    memosFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            
            importDatabase(booksFile, memosFile)
        } catch (e: Exception) {
            Log.e("DatabaseExporter", "Failed to import dummy data", e)
            Result.failure(e)
        }
    }
} 