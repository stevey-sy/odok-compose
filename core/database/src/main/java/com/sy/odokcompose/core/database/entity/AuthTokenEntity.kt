package com.sy.odokcompose.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "auth_tokens",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["tokenType"]),
        Index(value = ["expiresAt"])
    ]
)
data class AuthTokenEntity(
    @PrimaryKey val tokenId: String = UUID.randomUUID().toString(),
    val userId: String, // UserEntity의 UUID 참조
    val tokenType: String, // "ACCESS", "REFRESH", "ID_TOKEN"
    val token: String, // JWT 토큰 문자열 (암호화 저장 권장)
    val expiresAt: Long, // 만료 시간 (timestamp)
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val scopes: String? = null, // OAuth scopes (JSON 형태)
    val refreshCount: Int = 0, // 갱신 횟수
    val lastUsedAt: Long = System.currentTimeMillis()
) {
    
    companion object {
        const val TOKEN_TYPE_ACCESS = "ACCESS"
        const val TOKEN_TYPE_REFRESH = "REFRESH" 
        const val TOKEN_TYPE_ID = "ID_TOKEN"
        
        fun createAccessToken(
            userId: String,
            token: String,
            expiresAt: Long,
            scopes: String? = null
        ) = AuthTokenEntity(
            userId = userId,
            tokenType = TOKEN_TYPE_ACCESS,
            token = token,
            expiresAt = expiresAt,
            scopes = scopes
        )
        
        fun createRefreshToken(
            userId: String,
            token: String,
            expiresAt: Long
        ) = AuthTokenEntity(
            userId = userId,
            tokenType = TOKEN_TYPE_REFRESH,
            token = token,
            expiresAt = expiresAt
        )
        
        fun createIdToken(
            userId: String,
            token: String,
            expiresAt: Long
        ) = AuthTokenEntity(
            userId = userId,
            tokenType = TOKEN_TYPE_ID,
            token = token,
            expiresAt = expiresAt
        )
    }
    
    /**
     * 토큰이 만료되었는지 확인
     */
    fun isExpired(): Boolean {
        return System.currentTimeMillis() > expiresAt
    }
    
    /**
     * 토큰이 곧 만료될 예정인지 확인 (5분 이내)
     */
    fun isExpiringSoon(thresholdMinutes: Long = 5): Boolean {
        val thresholdMillis = thresholdMinutes * 60 * 1000
        return (expiresAt - System.currentTimeMillis()) <= thresholdMillis
    }
    
    /**
     * 토큰 사용 기록 업데이트
     */
    fun markAsUsed(): AuthTokenEntity {
        return this.copy(
            lastUsedAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
    
    /**
     * 토큰 비활성화
     */
    fun deactivate(): AuthTokenEntity {
        return this.copy(
            isActive = false,
            updatedAt = System.currentTimeMillis()
        )
    }
    
    /**
     * 리프레시 카운트 증가
     */
    fun incrementRefreshCount(): AuthTokenEntity {
        return this.copy(
            refreshCount = refreshCount + 1,
            updatedAt = System.currentTimeMillis()
        )
    }
}