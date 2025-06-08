package com.sy.odokcompose.core.database.export

import android.content.Context
import android.util.Log
import com.sy.odokcompose.core.database.BookDao
import kotlinx.coroutines.flow.first
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseExporter @Inject constructor(
    private val context: Context,
    private val bookDao: BookDao,
    private val bookJsonExporter: BookJsonExporter
) {
    suspend fun exportBooks(): Result<File> {
        return try {
            Log.d("DatabaseExporter", "Starting export process...")
            val books = bookDao.getAllBooks().first()
            Log.d("DatabaseExporter", "Retrieved ${books.size} books from database")
            
            val exportDir = File(context.filesDir, "exports")
            val dirCreated = exportDir.mkdirs()
            Log.d("DatabaseExporter", "Export directory created: $dirCreated at ${exportDir.absolutePath}")
            
            val timestamp = System.currentTimeMillis()
            val outputFile = File(exportDir, "books_$timestamp.json")
            Log.d("DatabaseExporter", "Output file path: ${outputFile.absolutePath}")
            
            bookJsonExporter.exportBooks(books, outputFile)
            
            // 파일 생성 여부 확인
            if (outputFile.exists()) {
                Log.d("DatabaseExporter", "File exists: ${outputFile.absolutePath}")
                Log.d("DatabaseExporter", "File size: ${outputFile.length()} bytes")
                Log.d("DatabaseExporter", "File content preview: ${outputFile.readText().take(100)}")
            } else {
                Log.e("DatabaseExporter", "File does not exist: ${outputFile.absolutePath}")
            }
            
            Log.d("DatabaseExporter", "Export completed successfully")
            Result.success(outputFile)
        } catch (e: Exception) {
            Log.e("DatabaseExporter", "Export failed", e)
            Result.failure(e)
        }
    }
    
    suspend fun importBooks(inputFile: File): Result<Unit> {
        return try {
            val books = bookJsonExporter.importBooks(inputFile).getOrThrow()
            books.forEach { book ->
                bookDao.insertBook(book)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importDummyData(): Result<Unit> {
        return try {
            val dummyFile = File(context.filesDir, "books_dummy.json")
            if (!dummyFile.exists()) {
                // assets에서 더미 데이터 파일을 복사
                context.assets.open("books_dummy.json").use { input ->
                    dummyFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            
            importBooks(dummyFile)
        } catch (e: Exception) {
            Log.e("DatabaseExporter", "Failed to import dummy data", e)
            Result.failure(e)
        }
    }
} 