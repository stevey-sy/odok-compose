package com.sy.odokcompose.core.database.dao

import androidx.room.*
import com.sy.odokcompose.core.database.entity.AuthTokenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthTokenDao {
    
    // 기본 CRUD 작업
    
    @Query("SELECT * FROM auth_tokens WHERE tokenId = :tokenId")
    suspend fun getById(tokenId: String): AuthTokenEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(token: AuthTokenEntity): Long
    
    @Update
    suspend fun update(token: AuthTokenEntity)
    
    @Delete
    suspend fun delete(token: AuthTokenEntity)
    
    @Query("DELETE FROM auth_tokens WHERE tokenId = :tokenId")
    suspend fun deleteById(tokenId: String)
    
    // 토큰 타입별 조회
    
    @Query("""
        SELECT * FROM auth_tokens 
        WHERE userId = :userId 
        AND tokenType = :tokenType 
        AND isActive = 1 
        ORDER BY createdAt DESC 
        LIMIT 1
    """)
    suspend fun getLatestTokenByType(userId: String, tokenType: String): AuthTokenEntity?
    
    @Query("""
        SELECT * FROM auth_tokens 
        WHERE userId = :userId 
        AND tokenType = :tokenType 
        AND isActive = 1 
        ORDER BY createdAt DESC 
        LIMIT 1
    """)
    fun getLatestTokenByTypeFlow(userId: String, tokenType: String): Flow<AuthTokenEntity?>
    
    @Query("""
        SELECT * FROM auth_tokens 
        WHERE userId = :userId 
        AND tokenType = 'ACCESS' 
        AND isActive = 1 
        AND expiresAt > :currentTime
        ORDER BY createdAt DESC 
        LIMIT 1
    """)
    suspend fun getValidAccessToken(userId: String, currentTime: Long = System.currentTimeMillis()): AuthTokenEntity?
    
    @Query("""
        SELECT * FROM auth_tokens 
        WHERE userId = :userId 
        AND tokenType = 'REFRESH' 
        AND isActive = 1 
        AND expiresAt > :currentTime
        ORDER BY createdAt DESC 
        LIMIT 1
    """)
    suspend fun getValidRefreshToken(userId: String, currentTime: Long = System.currentTimeMillis()): AuthTokenEntity?
    
    // 토큰 관리
    
    @Query("""
        UPDATE auth_tokens 
        SET isActive = 0, updatedAt = :timestamp 
        WHERE userId = :userId AND tokenType = :tokenType
    """)
    suspend fun deactivateTokensByType(
        userId: String, 
        tokenType: String, 
        timestamp: Long = System.currentTimeMillis()
    )
    
    @Query("""
        UPDATE auth_tokens 
        SET isActive = 0, updatedAt = :timestamp 
        WHERE userId = :userId
    """)
    suspend fun deactivateAllUserTokens(
        userId: String, 
        timestamp: Long = System.currentTimeMillis()
    )
    
    @Query("""
        UPDATE auth_tokens 
        SET lastUsedAt = :timestamp, updatedAt = :timestamp 
        WHERE tokenId = :tokenId
    """)
    suspend fun updateLastUsedTime(
        tokenId: String, 
        timestamp: Long = System.currentTimeMillis()
    )
    
    @Query("""
        UPDATE auth_tokens 
        SET refreshCount = refreshCount + 1, updatedAt = :timestamp 
        WHERE tokenId = :tokenId
    """)
    suspend fun incrementRefreshCount(
        tokenId: String, 
        timestamp: Long = System.currentTimeMillis()
    )
    
    // 만료된 토큰 관리
    
    @Query("SELECT * FROM auth_tokens WHERE expiresAt <= :currentTime")
    suspend fun getExpiredTokens(currentTime: Long = System.currentTimeMillis()): List<AuthTokenEntity>
    
    @Query("DELETE FROM auth_tokens WHERE expiresAt <= :currentTime")
    suspend fun deleteExpiredTokens(currentTime: Long = System.currentTimeMillis()): Int
    
    @Query("""
        SELECT * FROM auth_tokens 
        WHERE expiresAt BETWEEN :currentTime AND :thresholdTime 
        AND isActive = 1
    """)
    suspend fun getExpiringSoonTokens(
        currentTime: Long = System.currentTimeMillis(),
        thresholdTime: Long = System.currentTimeMillis() + (5 * 60 * 1000) // 5분 후
    ): List<AuthTokenEntity>
    
    // 사용자별 토큰 조회
    
    @Query("""
        SELECT * FROM auth_tokens 
        WHERE userId = :userId 
        ORDER BY createdAt DESC
    """)
    suspend fun getAllTokensForUser(userId: String): List<AuthTokenEntity>
    
    @Query("""
        SELECT * FROM auth_tokens 
        WHERE userId = :userId 
        AND isActive = 1 
        ORDER BY createdAt DESC
    """)
    suspend fun getActiveTokensForUser(userId: String): List<AuthTokenEntity>
    
    @Query("""
        SELECT * FROM auth_tokens 
        WHERE userId = :userId 
        ORDER BY createdAt DESC
    """)
    fun getAllTokensForUserFlow(userId: String): Flow<List<AuthTokenEntity>>
    
    // 토큰 검증
    
    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM auth_tokens 
            WHERE token = :token 
            AND isActive = 1 
            AND expiresAt > :currentTime
        )
    """)
    suspend fun isTokenValid(token: String, currentTime: Long = System.currentTimeMillis()): Boolean
    
    @Query("""
        SELECT userId FROM auth_tokens 
        WHERE token = :token 
        AND isActive = 1 
        AND expiresAt > :currentTime
        LIMIT 1
    """)
    suspend fun getUserIdByValidToken(token: String, currentTime: Long = System.currentTimeMillis()): String?
    
    // 통계 및 유틸리티
    
    @Query("SELECT COUNT(*) FROM auth_tokens WHERE userId = :userId AND isActive = 1")
    suspend fun getActiveTokenCount(userId: String): Int
    
    @Query("SELECT COUNT(*) FROM auth_tokens WHERE userId = :userId")
    suspend fun getTotalTokenCount(userId: String): Int
    
    @Query("""
        SELECT COUNT(*) FROM auth_tokens 
        WHERE expiresAt <= :currentTime
    """)
    suspend fun getExpiredTokenCount(currentTime: Long = System.currentTimeMillis()): Int
    
    // 보안 관련
    
    @Query("""
        SELECT * FROM auth_tokens 
        WHERE userId = :userId 
        AND refreshCount > :maxRefreshCount
    """)
    suspend fun getOverRefreshedTokens(userId: String, maxRefreshCount: Int = 10): List<AuthTokenEntity>
    
    @Query("""
        SELECT * FROM auth_tokens 
        WHERE lastUsedAt < :thresholdTime 
        AND isActive = 1
    """)
    suspend fun getUnusedTokens(thresholdTime: Long): List<AuthTokenEntity>
    
    // 개발/테스트용
    
    @Query("DELETE FROM auth_tokens")
    suspend fun deleteAll()
    
    @Query("DELETE FROM auth_tokens WHERE userId = :userId")
    suspend fun deleteAllTokensForUser(userId: String)
    
    @Query("SELECT * FROM auth_tokens ORDER BY createdAt DESC")
    suspend fun getAllTokens(): List<AuthTokenEntity>
    
    // 트랜잭션 메서드
    
    @Transaction
    suspend fun replaceAccessToken(userId: String, newToken: AuthTokenEntity) {
        // 기존 ACCESS 토큰들 비활성화
        deactivateTokensByType(userId, AuthTokenEntity.TOKEN_TYPE_ACCESS)
        // 새 토큰 삽입
        insert(newToken)
    }
    
    @Transaction
    suspend fun replaceRefreshToken(userId: String, newToken: AuthTokenEntity) {
        // 기존 REFRESH 토큰들 비활성화
        deactivateTokensByType(userId, AuthTokenEntity.TOKEN_TYPE_REFRESH)
        // 새 토큰 삽입
        insert(newToken)
    }
    
    @Transaction
    suspend fun cleanupExpiredTokens(): Int {
        val expiredCount = getExpiredTokenCount()
        deleteExpiredTokens()
        return expiredCount
    }
}