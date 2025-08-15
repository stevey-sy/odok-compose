package com.sy.odokcompose.core.supabase.sync.model

/**
 * 동기화 결과를 나타내는 sealed class
 */
sealed class SyncResult<out T> {
    
    /**
     * 동기화 성공
     * @param data 동기화 결과 데이터
     * @param syncedCount 동기화된 항목 수
     * @param conflictCount 충돌이 발생한 항목 수
     */
    data class Success<T>(
        val data: T,
        val syncedCount: Int = 0,
        val conflictCount: Int = 0
    ) : SyncResult<T>()
    
    /**
     * 동기화 실패
     * @param exception 발생한 예외
     * @param message 에러 메시지
     * @param failedItems 동기화에 실패한 항목들
     */
    data class Error<T>(
        val exception: Throwable,
        val message: String = exception.message ?: "동기화에 실패했습니다",
        val failedItems: List<T> = emptyList()
    ) : SyncResult<T>()
    
    /**
     * 동기화 진행 중
     * @param progress 진행률 (0.0 ~ 1.0)
     * @param currentItem 현재 처리 중인 항목 정보
     */
    data class Progress<T>(
        val progress: Float,
        val currentItem: String = ""
    ) : SyncResult<T>()
    
    /**
     * 성공인지 확인
     */
    val isSuccess: Boolean
        get() = this is Success
    
    /**
     * 실패인지 확인
     */
    val isError: Boolean
        get() = this is Error
    
    /**
     * 진행 중인지 확인
     */
    val isProgress: Boolean
        get() = this is Progress
    
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
 * 동기화 상태
 */
enum class SyncStatus {
    IDLE,           // 대기 중
    SYNCING,        // 동기화 중
    SUCCESS,        // 성공
    ERROR,          // 에러
    CONFLICT        // 충돌 발생
}

/**
 * 충돌 해결 전략
 */
enum class ConflictResolution {
    LOCAL_WINS,     // 로컬 데이터 우선
    REMOTE_WINS,    // 원격 데이터 우선
    LAST_WRITE_WINS, // 최신 수정 시간 우선
    MANUAL          // 수동 해결 필요
}

/**
 * 동기화 통계 정보
 */
data class SyncStats(
    val totalItems: Int = 0,
    val syncedItems: Int = 0,
    val failedItems: Int = 0,
    val conflictItems: Int = 0,
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val duration: Long = 0L
) {
    val successRate: Float
        get() = if (totalItems > 0) syncedItems.toFloat() / totalItems else 0f
    
    val isComplete: Boolean
        get() = syncedItems + failedItems + conflictItems == totalItems
}