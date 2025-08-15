package com.sy.odokcompose.core.supabase.model

import kotlinx.serialization.Serializable

/**
 * Supabase API 응답을 래핑하는 클래스
 */
sealed class SupabaseResponse<out T> {
    
    /**
     * 성공 응답
     * @param data 응답 데이터
     */
    data class Success<T>(val data: T) : SupabaseResponse<T>()
    
    /**
     * 에러 응답
     * @param exception 발생한 예외
     * @param message 에러 메시지
     * @param code HTTP 상태 코드 또는 에러 코드
     */
    data class Error(
        val exception: Throwable,
        val message: String = exception.message ?: "알 수 없는 오류가 발생했습니다",
        val code: String? = null
    ) : SupabaseResponse<Nothing>()
    
    /**
     * 로딩 상태
     */
    object Loading : SupabaseResponse<Nothing>()
    
    /**
     * 성공인지 확인
     */
    val isSuccess: Boolean
        get() = this is Success
    
    /**
     * 에러인지 확인
     */
    val isError: Boolean
        get() = this is Error
    
    /**
     * 로딩 중인지 확인
     */
    val isLoading: Boolean
        get() = this is Loading
    
    /**
     * 성공 시 데이터 반환, 실패 시 null
     */
    fun getOrNull(): T? {
        return if (this is Success) data else null
    }
    
    /**
     * 실패 시 예외 반환, 성공 시 null
     */
    fun exceptionOrNull(): Throwable? {
        return if (this is Error) exception else null
    }
}

/**
 * 동기화 관련 응답 정보
 */
@Serializable
data class SyncInfo(
    val totalCount: Int,
    val syncedCount: Int,
    val failedCount: Int,
    val lastSyncAt: String, // ISO 8601 format
    val conflicts: List<ConflictInfo> = emptyList()
)

/**
 * 동기화 충돌 정보
 */
@Serializable
data class ConflictInfo(
    val id: String,
    val tableName: String,
    val localUpdatedAt: String,
    val remoteUpdatedAt: String,
    val resolution: String // "local_wins", "remote_wins", "manual_required"
)