package com.sy.odokcompose.core.supabase.sync

import com.sy.odokcompose.core.supabase.sync.model.SyncResult
import com.sy.odokcompose.core.supabase.sync.model.SyncStats
import kotlinx.coroutines.flow.Flow

/**
 * Supabase 동기화 서비스 인터페이스
 * 
 * 로컬 Room 데이터베이스와 Supabase 원격 데이터베이스 간의 
 * 양방향 동기화를 담당합니다.
 */
interface SupabaseSyncService {
    
    /**
     * 동기화 상태 Flow
     */
    val syncStatus: Flow<SyncResult<SyncStats>>
    
    /**
     * 전체 데이터 동기화 (양방향)
     * 
     * @param userId 사용자 ID
     * @param forceSync 강제 동기화 (마지막 동기화 시간 무시)
     * @return 동기화 결과
     */
    suspend fun syncAll(userId: String, forceSync: Boolean = false): SyncResult<SyncStats>
    
    /**
     * 책 데이터 동기화
     * 
     * @param userId 사용자 ID
     * @param forceSync 강제 동기화
     * @return 동기화 결과
     */
    suspend fun syncBooks(userId: String, forceSync: Boolean = false): SyncResult<SyncStats>
    
    /**
     * 사용자 프로필 동기화
     * 
     * @param userId 사용자 ID
     * @return 동기화 결과
     */
    suspend fun syncUserProfile(userId: String): SyncResult<SyncStats>
    
    /**
     * 로컬에서 원격으로 푸시 (업로드)
     * 
     * @param userId 사용자 ID
     * @return 푸시 결과
     */
    suspend fun pushLocalChanges(userId: String): SyncResult<SyncStats>
    
    /**
     * 원격에서 로컬로 풀 (다운로드)
     * 
     * @param userId 사용자 ID
     * @return 풀 결과
     */
    suspend fun pullRemoteChanges(userId: String): SyncResult<SyncStats>
    
    /**
     * 마지막 동기화 시간 가져오기
     * 
     * @param userId 사용자 ID
     * @return 마지막 동기화 시간 (Unix timestamp)
     */
    suspend fun getLastSyncTime(userId: String): Long
    
    /**
     * 동기화 필요 여부 확인
     * 
     * @param userId 사용자 ID
     * @return 동기화가 필요하면 true
     */
    suspend fun needsSync(userId: String): Boolean
    
    /**
     * 현재 동기화 취소
     */
    suspend fun cancelSync()
    
    /**
     * 오프라인 변경사항 대기열 크기 조회
     * 
     * @param userId 사용자 ID
     * @return 대기 중인 변경사항 개수
     */
    suspend fun getPendingChangesCount(userId: String): Int
}