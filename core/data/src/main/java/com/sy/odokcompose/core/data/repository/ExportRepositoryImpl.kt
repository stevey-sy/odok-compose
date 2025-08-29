package com.sy.odokcompose.core.data.repository

import com.sy.odokcompose.core.database.export.DatabaseExporter
import com.sy.odokcompose.core.supabase.auth.SupabaseAuthService
import java.io.File
import javax.inject.Inject

class ExportRepositoryImpl @Inject constructor(
    private val databaseExporter: DatabaseExporter,
    private val authService: SupabaseAuthService
) : ExportRepository {
    
    override suspend fun exportDatabase(): Result<File> {
        val currentUser = authService.getCurrentUser()
        val userId = currentUser?.id ?: return Result.failure(
            IllegalStateException("사용자가 로그인되어 있지 않습니다")
        )
        
        return databaseExporter.exportDatabase(userId)
    }
    
    override suspend fun importDatabase(booksFile: File, memosFile: File): Result<Unit> {
        // importDatabase는 사용자별 데이터가 아니므로 기존 방식 유지
        return databaseExporter.importDatabase(booksFile, memosFile)
    }
    
    override suspend fun importDummyData(): Result<Unit> {
        val currentUser = authService.getCurrentUser()
        val userId = currentUser?.id ?: return Result.failure(
            IllegalStateException("사용자가 로그인되어 있지 않습니다")
        )
        
        return databaseExporter.importDummyData(userId)
    }
}