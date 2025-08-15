package com.sy.odokcompose.core.supabase.mapper

import com.sy.odokcompose.core.database.entity.UserEntity
import com.sy.odokcompose.core.supabase.model.SupabaseUser
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * UserEntity ↔ SupabaseUser 변환 매퍼
 */
object UserMapper {
    
    private val isoFormatter = DateTimeFormatter.ISO_INSTANT
    
    /**
     * UserEntity를 SupabaseUser로 변환
     */
    fun toSupabaseUser(entity: UserEntity): SupabaseUser {
        return SupabaseUser(
            userId = entity.userId,
            email = entity.email,
            displayName = entity.displayName,
            profileImageUrl = entity.profileImageUrl,
            provider = entity.provider,
            preferredLanguage = entity.preferredLanguage,
            timezone = entity.timezone,
            isActive = entity.isActive,
            createdAt = timestampToIsoString(entity.createdAt),
            updatedAt = timestampToIsoString(entity.updatedAt),
            lastSyncAt = timestampToIsoString(entity.lastSyncAt)
        )
    }
    
    /**
     * SupabaseUser를 UserEntity로 변환
     */
    fun toUserEntity(supabaseUser: SupabaseUser): UserEntity {
        return UserEntity(
            userId = supabaseUser.userId,
            email = supabaseUser.email,
            displayName = supabaseUser.displayName,
            profileImageUrl = supabaseUser.profileImageUrl,
            provider = supabaseUser.provider,
            preferredLanguage = supabaseUser.preferredLanguage,
            timezone = supabaseUser.timezone,
            isActive = supabaseUser.isActive,
            createdAt = isoStringToTimestamp(supabaseUser.createdAt),
            updatedAt = isoStringToTimestamp(supabaseUser.updatedAt),
            lastSyncAt = isoStringToTimestamp(supabaseUser.lastSyncAt)
        )
    }
    
    /**
     * Unix timestamp (milliseconds)를 ISO 8601 문자열로 변환
     */
    private fun timestampToIsoString(timestamp: Long): String {
        return Instant.ofEpochMilli(timestamp).atOffset(ZoneOffset.UTC).format(isoFormatter)
    }
    
    /**
     * ISO 8601 문자열을 Unix timestamp (milliseconds)로 변환
     */
    private fun isoStringToTimestamp(isoString: String): Long {
        return try {
            Instant.from(isoFormatter.parse(isoString)).toEpochMilli()
        } catch (e: Exception) {
            // 파싱 실패 시 현재 시간 반환
            System.currentTimeMillis()
        }
    }
}