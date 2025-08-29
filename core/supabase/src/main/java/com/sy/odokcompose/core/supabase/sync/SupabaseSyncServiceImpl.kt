package com.sy.odokcompose.core.supabase.sync

import android.util.Log
import com.sy.odokcompose.core.database.BookDao
import com.sy.odokcompose.core.database.dao.UserDao
import com.sy.odokcompose.core.database.entity.BookEntity
import com.sy.odokcompose.core.database.entity.UserEntity
import com.sy.odokcompose.core.supabase.mapper.BookMapper
import com.sy.odokcompose.core.supabase.mapper.UserMapper
import com.sy.odokcompose.core.supabase.repository.SupabaseBookRepository
import com.sy.odokcompose.core.supabase.sync.model.ConflictResolution
import com.sy.odokcompose.core.supabase.sync.model.SyncResult
import com.sy.odokcompose.core.supabase.sync.model.SyncStats
import com.sy.odokcompose.core.supabase.sync.model.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Supabase 동기화 서비스 구현
 */
@Singleton
class SupabaseSyncServiceImpl @Inject constructor(
    private val bookDao: BookDao,
    private val userDao: UserDao,
    private val supabaseBookRepository: SupabaseBookRepository
) : SupabaseSyncService {
    
    private val _syncStatus = MutableStateFlow<SyncResult<SyncStats>>(
        SyncResult.Success(SyncStats())
    )
    override val syncStatus: Flow<SyncResult<SyncStats>> = _syncStatus.asStateFlow()
    
    @Volatile
    private var isSyncing = false
    
    companion object {
        private const val TAG = "SupabaseSyncService"
        private const val SYNC_THRESHOLD_MS = 5 * 60 * 1000L // 5분
    }
    
    override suspend fun syncAll(userId: String, forceSync: Boolean): SyncResult<SyncStats> {
        if (isSyncing) {
            Log.w(TAG, "동기화가 이미 진행 중입니다")
            return SyncResult.Error(
                exception = IllegalStateException("동기화가 이미 진행 중입니다"),
                message = "동기화가 이미 진행 중입니다"
            )
        }
        
        return try {
            isSyncing = true
            val startTime = System.currentTimeMillis()
            
            Log.d(TAG, "전체 동기화 시작: $userId")
            
            _syncStatus.value = SyncResult.Progress<SyncStats>(
                progress = 0.1f,
                currentItem = "동기화 준비 중..."
            )
            
            // 1. 사용자 프로필 동기화
            val userSyncResult = syncUserProfile(userId)
            if (userSyncResult.isError) {
                Log.e(TAG, "사용자 프로필 동기화 실패")
                return userSyncResult
            }
            
            _syncStatus.value = SyncResult.Progress<SyncStats>(
                progress = 0.3f,
                currentItem = "책 데이터 동기화 중..."
            )
            
            // 2. 책 데이터 동기화
            val bookSyncResult = syncBooks(userId, forceSync)
            if (bookSyncResult.isError) {
                Log.e(TAG, "책 데이터 동기화 실패")
                return bookSyncResult
            }
            
            _syncStatus.value = SyncResult.Progress<SyncStats>(
                progress = 0.9f,
                currentItem = "동기화 완료 중..."
            )
            
            // 3. 동기화 시간 업데이트
            updateLastSyncTime(userId)
            
            val endTime = System.currentTimeMillis()
            val finalStats = SyncStats(
                totalItems = (userSyncResult.getOrNull()?.totalItems ?: 0) + 
                            (bookSyncResult.getOrNull()?.totalItems ?: 0),
                syncedItems = (userSyncResult.getOrNull()?.syncedItems ?: 0) + 
                             (bookSyncResult.getOrNull()?.syncedItems ?: 0),
                failedItems = (userSyncResult.getOrNull()?.failedItems ?: 0) + 
                             (bookSyncResult.getOrNull()?.failedItems ?: 0),
                conflictItems = (userSyncResult.getOrNull()?.conflictItems ?: 0) + 
                               (bookSyncResult.getOrNull()?.conflictItems ?: 0),
                startTime = startTime,
                endTime = endTime,
                duration = endTime - startTime
            )
            
            Log.d(TAG, "전체 동기화 완료: ${finalStats.syncedItems}/${finalStats.totalItems}")
            
            val result = SyncResult.Success(finalStats)
            _syncStatus.value = result
            result
            
        } catch (e: Exception) {
            Log.e(TAG, "전체 동기화 실패", e)
            val result = SyncResult.Error<SyncStats>(
                exception = e,
                message = "동기화에 실패했습니다: ${e.message}"
            )
            _syncStatus.value = result
            result
        } finally {
            isSyncing = false
        }
    }
    
    override suspend fun syncBooks(userId: String, forceSync: Boolean): SyncResult<SyncStats> {
        return try {
            Log.d(TAG, "책 데이터 동기화 시작: $userId")
            val startTime = System.currentTimeMillis()
            
            // 1. 로컬 변경사항을 원격으로 푸시
            val pushResult = pushLocalBookChanges(userId)
            if (pushResult.isError) {
                return pushResult
            }
            
            // 2. 원격 변경사항을 로컬로 풀
            val pullResult = pullRemoteBookChanges(userId)
            if (pullResult.isError) {
                return pullResult
            }
            
            val endTime = System.currentTimeMillis()
            val pushStats = pushResult.getOrNull() ?: SyncStats()
            val pullStats = pullResult.getOrNull() ?: SyncStats()
            
            val combinedStats = SyncStats(
                totalItems = pushStats.totalItems + pullStats.totalItems,
                syncedItems = pushStats.syncedItems + pullStats.syncedItems,
                failedItems = pushStats.failedItems + pullStats.failedItems,
                conflictItems = pushStats.conflictItems + pullStats.conflictItems,
                startTime = startTime,
                endTime = endTime,
                duration = endTime - startTime
            )
            
            Log.d(TAG, "책 데이터 동기화 완료: ${combinedStats.syncedItems}/${combinedStats.totalItems}")
            SyncResult.Success(combinedStats)
            
        } catch (e: Exception) {
            Log.e(TAG, "책 데이터 동기화 실패", e)
            SyncResult.Error(
                exception = e,
                message = "책 데이터 동기화에 실패했습니다: ${e.message}"
            )
        }
    }
    
    override suspend fun syncUserProfile(userId: String): SyncResult<SyncStats> {
        return try {
            Log.d(TAG, "사용자 프로필 동기화: $userId")
            
            val user = userDao.getById(userId)
            if (user != null) {
                // 로컬 사용자 정보가 있으면 성공으로 처리
                // 실제로는 원격과 비교해서 동기화해야 함
                SyncResult.Success(
                    SyncStats(
                        totalItems = 1,
                        syncedItems = 1,
                        startTime = System.currentTimeMillis(),
                        endTime = System.currentTimeMillis()
                    )
                )
            } else {
                SyncResult.Error(
                    exception = IllegalStateException("사용자를 찾을 수 없습니다"),
                    message = "사용자 정보를 찾을 수 없습니다"
                )
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "사용자 프로필 동기화 실패", e)
            SyncResult.Error(
                exception = e,
                message = "사용자 프로필 동기화에 실패했습니다: ${e.message}"
            )
        }
    }
    
    override suspend fun pushLocalChanges(userId: String): SyncResult<SyncStats> {
        return pushLocalBookChanges(userId)
    }
    
    override suspend fun pullRemoteChanges(userId: String): SyncResult<SyncStats> {
        return pullRemoteBookChanges(userId)
    }
    
    override suspend fun getLastSyncTime(userId: String): Long {
        return try {
            val user = userDao.getById(userId)
            user?.lastSyncAt ?: 0L
        } catch (e: Exception) {
            Log.e(TAG, "마지막 동기화 시간 조회 실패", e)
            0L
        }
    }
    
    override suspend fun needsSync(userId: String): Boolean {
        val lastSyncTime = getLastSyncTime(userId)
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastSyncTime) > SYNC_THRESHOLD_MS
    }
    
    override suspend fun cancelSync() {
        isSyncing = false
        Log.d(TAG, "동기화 취소됨")
    }
    
    override suspend fun getPendingChangesCount(userId: String): Int {
        return try {
            val lastSyncTime = getLastSyncTime(userId)
            val changedBooks = bookDao.getBooksUpdatedSince(userId, lastSyncTime)
            changedBooks.size
        } catch (e: Exception) {
            Log.e(TAG, "대기 중인 변경사항 조회 실패", e)
            0
        }
    }
    
    /**
     * 로컬 책 변경사항을 원격으로 푸시
     */
    private suspend fun pushLocalBookChanges(userId: String): SyncResult<SyncStats> {
        return try {
            Log.d(TAG, "로컬 책 변경사항 푸시 시작")
            
            val lastSyncTime = getLastSyncTime(userId)
            val changedBooks = bookDao.getBooksUpdatedSince(userId, lastSyncTime)
            
            if (changedBooks.isEmpty()) {
                Log.d(TAG, "푸시할 변경사항 없음")
                return SyncResult.Success(SyncStats())
            }
            
            Log.d(TAG, "푸시할 책: ${changedBooks.size}개")
            
            var syncedCount = 0
            var failedCount = 0
            
            changedBooks.forEach { localBook ->
                val supabaseBook = BookMapper.toSupabaseBook(localBook)
                val result = supabaseBookRepository.upsertBooks(listOf(supabaseBook))
                
                if (result.isSuccess) {
                    syncedCount++
                } else {
                    failedCount++
                    Log.w(TAG, "책 푸시 실패: ${localBook.title}")
                }
            }
            
            Log.d(TAG, "로컬 변경사항 푸시 완료: $syncedCount/$changedBooks.size")
            
            SyncResult.Success(
                SyncStats(
                    totalItems = changedBooks.size,
                    syncedItems = syncedCount,
                    failedItems = failedCount
                )
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "로컬 변경사항 푸시 실패", e)
            SyncResult.Error(
                exception = e,
                message = "로컬 변경사항 업로드에 실패했습니다: ${e.message}"
            )
        }
    }
    
    /**
     * 원격 책 변경사항을 로컬로 풀
     */
    private suspend fun pullRemoteBookChanges(userId: String): SyncResult<SyncStats> {
        return try {
            Log.d(TAG, "원격 책 변경사항 풀 시작")
            
            val lastSyncTime = getLastSyncTime(userId)
            val lastSyncTimeIso = timestampToIsoString(lastSyncTime)
            
            val result = supabaseBookRepository.getBooksUpdatedSince(userId, lastSyncTime)
            
            if (result.isError) {
                return SyncResult.Error(
                    exception = result.exceptionOrNull() ?: Exception("원격 데이터 조회 실패"),
                    message = "원격 데이터를 가져오는데 실패했습니다"
                )
            }
            
            val remoteBooks = result.getOrNull() ?: emptyList()
            
            if (remoteBooks.isEmpty()) {
                Log.d(TAG, "풀할 변경사항 없음")
                return SyncResult.Success(SyncStats())
            }
            
            Log.d(TAG, "풀할 책: ${remoteBooks.size}개")
            
            var syncedCount = 0
            var conflictCount = 0
            
            remoteBooks.forEach { remoteBook ->
                val localBook = bookDao.getBookByIdSync(remoteBook.itemId)
                
                if (localBook == null) {
                    // 로컬에 없는 새 데이터 - 추가
                    val bookEntity = BookMapper.toBookEntity(remoteBook)
                    bookDao.insertBook(bookEntity)
                    syncedCount++
                } else {
                    // 충돌 해결 (Last-Write-Wins 전략)
                    val remoteEntity = BookMapper.toBookEntity(remoteBook)
                    val resolution = resolveConflict(localBook, remoteEntity)
                    
                    when (resolution) {
                        ConflictResolution.REMOTE_WINS, ConflictResolution.LAST_WRITE_WINS -> {
                            bookDao.updateBook(remoteEntity)
                            syncedCount++
                        }
                        ConflictResolution.LOCAL_WINS -> {
                            // 로컬 데이터 유지
                            syncedCount++
                        }
                        ConflictResolution.MANUAL -> {
                            conflictCount++
                            Log.w(TAG, "수동 해결 필요한 충돌: ${remoteBook.title}")
                        }
                    }
                }
            }
            
            Log.d(TAG, "원격 변경사항 풀 완료: $syncedCount/${remoteBooks.size}")
            
            SyncResult.Success(
                SyncStats(
                    totalItems = remoteBooks.size,
                    syncedItems = syncedCount,
                    conflictItems = conflictCount
                )
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "원격 변경사항 풀 실패", e)
            SyncResult.Error(
                exception = e,
                message = "원격 변경사항 다운로드에 실패했습니다: ${e.message}"
            )
        }
    }
    
    /**
     * 충돌 해결 전략 결정
     */
    private fun resolveConflict(local: BookEntity, remote: BookEntity): ConflictResolution {
        // Last-Write-Wins 전략: 최신 수정 시간을 가진 데이터 선택
        return if (remote.updatedAt > local.updatedAt) {
            ConflictResolution.REMOTE_WINS
        } else {
            ConflictResolution.LOCAL_WINS
        }
    }
    
    /**
     * 마지막 동기화 시간 업데이트
     */
    private suspend fun updateLastSyncTime(userId: String) {
        try {
            val currentTime = System.currentTimeMillis()
            val user = userDao.getById(userId)
            user?.let {
                val updatedUser = it.copy(
                    lastSyncAt = currentTime,
                    updatedAt = currentTime
                )
                userDao.update(updatedUser)
            }
        } catch (e: Exception) {
            Log.e(TAG, "동기화 시간 업데이트 실패", e)
        }
    }
    
    /**
     * Unix timestamp를 ISO 8601 문자열로 변환
     */
    private fun timestampToIsoString(timestamp: Long): String {
        return java.time.Instant.ofEpochMilli(timestamp)
            .atOffset(java.time.ZoneOffset.UTC)
            .format(java.time.format.DateTimeFormatter.ISO_INSTANT)
    }
}