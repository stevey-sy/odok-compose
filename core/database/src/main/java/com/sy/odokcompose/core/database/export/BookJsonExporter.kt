package com.sy.odokcompose.core.database.export

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Types
import com.sy.odokcompose.core.database.entity.BookEntity
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookJsonExporter @Inject constructor() {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    
    private val bookListType = Types.newParameterizedType(List::class.java, BookEntity::class.java)
    private val bookListAdapter = moshi.adapter<List<BookEntity>>(bookListType)
    
    suspend fun exportBooks(books: List<BookEntity>, outputFile: File): Result<Unit> {
        return try {
            Log.d("BookJsonExporter", "Converting ${books.size} books to JSON")
            val json = bookListAdapter.toJson(books)
            Log.d("BookJsonExporter", "JSON length: ${json.length}")
            
            outputFile.writeText(json)
            Log.d("BookJsonExporter", "JSON written to file: ${outputFile.absolutePath}")
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("BookJsonExporter", "Failed to export books", e)
            Result.failure(e)
        }
    }
    
    suspend fun importBooks(inputFile: File): Result<List<BookEntity>> {
        return try {
            val json = inputFile.readText()
            val books = bookListAdapter.fromJson(json)
            Result.success(books ?: emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 