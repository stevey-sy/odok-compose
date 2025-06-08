package com.sy.odokcompose.core.database.export

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.sy.odokcompose.core.database.entity.MemoEntity
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoJsonExporter @Inject constructor() {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    
    private val memoListType = Types.newParameterizedType(List::class.java, MemoEntity::class.java)
    private val memoListAdapter = moshi.adapter<List<MemoEntity>>(memoListType)
    
    suspend fun exportMemos(memos: List<MemoEntity>, outputFile: File): Result<Unit> {
        return try {
            Log.d("MemoJsonExporter", "Converting ${memos.size} memos to JSON")
            val json = memoListAdapter.toJson(memos)
            Log.d("MemoJsonExporter", "JSON length: ${json.length}")
            
            outputFile.writeText(json)
            Log.d("MemoJsonExporter", "JSON written to file: ${outputFile.absolutePath}")
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MemoJsonExporter", "Failed to export memos", e)
            Result.failure(e)
        }
    }
    
    suspend fun importMemos(inputFile: File): Result<List<MemoEntity>> {
        return try {
            val json = inputFile.readText()
            val memos = memoListAdapter.fromJson(json)
            Result.success(memos ?: emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 