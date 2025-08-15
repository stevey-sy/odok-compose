package com.sy.odokcompose.core.database.dao

import androidx.room.*
import com.sy.odokcompose.core.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    // 기본 CRUD 작업
    
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getById(userId: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getByEmail(email: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getByIdFlow(userId: String): Flow<UserEntity?>
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: UserEntity): Long
    
    @Update
    suspend fun update(user: UserEntity)
    
    @Delete
    suspend fun delete(user: UserEntity)
    
    @Query("DELETE FROM users WHERE userId = :userId")
    suspend fun deleteById(userId: String)
    
    // Upsert (Insert or Update)
    @Transaction
    suspend fun upsert(user: UserEntity) {
        val existingUser = getById(user.userId)
        if (existingUser == null) {
            insert(user)
        } else {
            update(user)
        }
    }
    
    // 동기화 관련 작업
    
    @Query("UPDATE users SET lastSyncAt = :timestamp WHERE userId = :userId")
    suspend fun updateLastSyncTime(userId: String, timestamp: Long)
    
    @Query("SELECT * FROM users WHERE updatedAt > :timestamp")
    suspend fun getUpdatedSince(timestamp: Long): List<UserEntity>
    
    @Query("SELECT * FROM users WHERE isActive = 1")
    suspend fun getActiveUsers(): List<UserEntity>
    
    @Query("UPDATE users SET isActive = :isActive WHERE userId = :userId")
    suspend fun updateActiveStatus(userId: String, isActive: Boolean)
    
    // 프로필 업데이트 관련
    
    @Query("""
        UPDATE users SET 
        displayName = :displayName,
        profileImageUrl = :profileImageUrl,
        updatedAt = :timestamp
        WHERE userId = :userId
    """)
    suspend fun updateProfile(
        userId: String, 
        displayName: String?, 
        profileImageUrl: String?,
        timestamp: Long = System.currentTimeMillis()
    )
    
    @Query("""
        UPDATE users SET 
        preferredLanguage = :language,
        updatedAt = :timestamp
        WHERE userId = :userId
    """)
    suspend fun updateLanguage(
        userId: String, 
        language: String,
        timestamp: Long = System.currentTimeMillis()
    )
    
    @Query("""
        UPDATE users SET 
        timezone = :timezone,
        updatedAt = :timestamp
        WHERE userId = :userId
    """)
    suspend fun updateTimezone(
        userId: String, 
        timezone: String,
        timestamp: Long = System.currentTimeMillis()
    )
    
    // 통계 및 유틸리티
    
    @Query("SELECT COUNT(*) FROM users WHERE isActive = 1")
    suspend fun getActiveUserCount(): Int
    
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getTotalUserCount(): Int
    
    @Query("SELECT * FROM users ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastCreatedUser(): UserEntity?
    
    // 로그인/인증 관련
    
    @Query("SELECT userId FROM users WHERE email = :email AND isActive = 1")
    suspend fun getUserIdByEmail(email: String): String?
    
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE userId = :userId AND isActive = 1)")
    suspend fun isUserActive(userId: String): Boolean
    
    // 개발/테스트용
    
    @Query("DELETE FROM users")
    suspend fun deleteAll()
    
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    suspend fun getAllUsers(): List<UserEntity>
    
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>
}