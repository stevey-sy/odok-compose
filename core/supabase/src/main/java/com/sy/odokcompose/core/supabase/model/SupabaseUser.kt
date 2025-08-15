package com.sy.odokcompose.core.supabase.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase에서 사용하는 User 모델
 * 
 * Room의 UserEntity와 매핑되는 원격 데이터 구조
 */
@Serializable
data class SupabaseUser(
    @SerialName("user_id") val userId: String,
    @SerialName("email") val email: String,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("profile_image_url") val profileImageUrl: String? = null,
    @SerialName("provider") val provider: String = "google",
    @SerialName("preferred_language") val preferredLanguage: String = "ko",
    @SerialName("timezone") val timezone: String = "Asia/Seoul",
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String, // ISO 8601 format
    @SerialName("updated_at") val updatedAt: String, // ISO 8601 format
    @SerialName("last_sync_at") val lastSyncAt: String  // ISO 8601 format
)