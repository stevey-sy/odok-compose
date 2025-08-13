package com.sy.odokcompose.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["isActive"]),
        Index(value = ["createdAt"]),
        Index(value = ["lastSyncAt"])
    ]
)
data class UserEntity(
    @PrimaryKey val userId: String, // Supabase UUID
    val email: String,
    val displayName: String? = null,
    val profileImageUrl: String? = null,
    val provider: String = "google", // OAuth provider (google, etc.)
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastSyncAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val preferredLanguage: String = "ko",
    val timezone: String = "Asia/Seoul"
)