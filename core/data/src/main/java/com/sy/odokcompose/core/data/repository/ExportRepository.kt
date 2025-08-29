package com.sy.odokcompose.core.data.repository

import java.io.File

interface ExportRepository {
    
    /**
     * 현재 사용자의 데이터베이스를 내보냅니다
     */
    suspend fun exportDatabase(): Result<File>
    
    /**
     * 데이터베이스를 가져옵니다
     */
    suspend fun importDatabase(booksFile: File, memosFile: File): Result<Unit>
    
    /**
     * 더미 데이터를 현재 사용자에게 가져옵니다
     */
    suspend fun importDummyData(): Result<Unit>
}