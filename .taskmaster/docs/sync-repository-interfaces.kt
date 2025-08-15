// ====================================
// SYNC REPOSITORY INTERFACES
// Supabase ↔ Room DB 동기화를 위한 인터페이스 정의
// ====================================

import kotlinx.coroutines.flow.Flow

// ====================================
// 기본 타입 정의
// ====================================

enum class EntityType {
    USER, BOOK, MEMO, TAG, BOOK_TAG_CROSS_REF
}

enum class SyncStatus {
    SYNCED,           // 동기화 완료
    PENDING_UPLOAD,   // 업로드 대기
    PENDING_DELETE,   // 삭제 대기
    SYNCING,          // 동기화 진행 중
    CONFLICT,         // 충돌 발생
    ERROR,            // 동기화 실패
    OFFLINE           // 오프라인 상태
}

sealed class SyncResult {
    object Success : SyncResult()
    data class Failure(val error: Throwable) : SyncResult()
    object Retry : SyncResult()
}

data class SyncMetadata(
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastSyncAt: Long = 0L,
    val remoteId: String? = null,
    val version: Int = 1,
    val conflictData: String? = null
)

// ====================================
// 동기화 가능한 엔티티 인터페이스
// ====================================

interface SyncableEntity {
    val id: Any
    val userId: String
    val createdAt: Long
    val updatedAt: Long
    val syncMetadata: SyncMetadata
}

// ====================================
// 핵심 동기화 매니저 인터페이스
// ====================================

interface SyncManager {
    /**
     * 앱 시작 시 초기 동기화 수행
     */
    suspend fun performInitialSync(): SyncResult
    
    /**
     * 특정 엔티티 타입 동기화
     */
    suspend fun syncEntity(entityType: EntityType): SyncResult
    
    /**
     * 모든 엔티티 동기화
     */
    suspend fun syncAllEntities(): SyncResult
    
    /**
     * 실시간 동기화 시작
     */
    fun startRealTimeSync()
    
    /**
     * 실시간 동기화 중지
     */
    fun stopRealTimeSync()
    
    /**
     * 동기화 상태 모니터링
     */
    val syncStatus: Flow<SyncStatus>
    
    /**
     * 동기화 진행률
     */
    val syncProgress: Flow<SyncProgress>
    
    /**
     * 동기화 큐에 엔티티 추가
     */
    suspend fun queueForSync(entityType: EntityType, entityId: Any)
    
    /**
     * 충돌 해결 후 재동기화
     */
    suspend fun retryAfterConflictResolution(entityType: EntityType, entityId: Any): SyncResult
}

data class SyncProgress(
    val currentEntity: EntityType,
    val completed: Int,
    val total: Int,
    val isComplete: Boolean
)

// ====================================
// 엔티티별 동기화 인터페이스
// ====================================

interface EntitySyncer<T : SyncableEntity> {
    /**
     * 로컬 변경사항을 원격으로 업로드
     */
    suspend fun pushLocalChanges(): SyncResult
    
    /**
     * 원격 변경사항을 로컬로 다운로드
     */
    suspend fun pullRemoteChanges(): SyncResult
    
    /**
     * 충돌 해결
     */
    suspend fun resolveConflicts(): SyncResult
    
    /**
     * 특정 엔티티 동기화
     */
    suspend fun syncEntity(entityId: Any): SyncResult
    
    /**
     * 배치 동기화
     */
    suspend fun syncBatch(entityIds: List<Any>): SyncResult
}

// ====================================
// 데이터소스 인터페이스
// ====================================

interface LocalDataSource<T : SyncableEntity> {
    /**
     * 모든 엔티티 조회
     */
    fun getAll(): Flow<List<T>>
    
    /**
     * ID로 엔티티 조회
     */
    suspend fun getById(id: Any): T?
    
    /**
     * 동기화 대기 중인 엔티티들 조회
     */
    suspend fun getPendingSync(): List<T>
    
    /**
     * 특정 상태의 엔티티들 조회
     */
    suspend fun getBySyncStatus(status: SyncStatus): List<T>
    
    /**
     * 마지막 동기화 시간 이후 수정된 엔티티들
     */
    suspend fun getModifiedSince(timestamp: Long): List<T>
    
    /**
     * 엔티티 삽입
     */
    suspend fun insert(entity: T): Long
    
    /**
     * 엔티티 업데이트
     */
    suspend fun update(entity: T)
    
    /**
     * 엔티티 삭제
     */
    suspend fun delete(entity: T)
    
    /**
     * 배치 업서트
     */
    suspend fun upsertAll(entities: List<T>)
    
    /**
     * 동기화 상태 업데이트
     */
    suspend fun updateSyncStatus(id: Any, status: SyncStatus)
    
    /**
     * 마지막 동기화 시간 업데이트
     */
    suspend fun updateLastSyncTime(timestamp: Long)
    
    /**
     * 마지막 동기화 시간 조회
     */
    suspend fun getLastSyncTime(): Long
}

interface RemoteDataSource<T : SyncableEntity> {
    /**
     * 사용자의 모든 엔티티 조회
     */
    suspend fun getAllForUser(userId: String): List<T>
    
    /**
     * 특정 시점 이후 수정된 엔티티들
     */
    suspend fun getUpdatedSince(userId: String, timestamp: Long): List<T>
    
    /**
     * ID로 엔티티 조회
     */
    suspend fun getById(id: Any): T?
    
    /**
     * 엔티티 생성
     */
    suspend fun create(entity: T): T
    
    /**
     * 엔티티 업데이트
     */
    suspend fun update(entity: T): T
    
    /**
     * 엔티티 삭제
     */
    suspend fun delete(id: Any)
    
    /**
     * 배치 업서트
     */
    suspend fun upsertBatch(entities: List<T>): List<T>
    
    /**
     * 엔티티 버전 조회 (충돌 감지용)
     */
    suspend fun getVersion(id: Any): Int?
    
    /**
     * 실시간 변경사항 구독
     */
    fun subscribeToChanges(userId: String): Flow<RemoteChangeEvent<T>>
}

data class RemoteChangeEvent<T>(
    val eventType: ChangeEventType,
    val entity: T?,
    val entityId: Any,
    val timestamp: Long
)

enum class ChangeEventType {
    INSERT, UPDATE, DELETE
}

// ====================================
// Repository 인터페이스 (Clean Architecture)
// ====================================

interface SyncableRepository<T : SyncableEntity> {
    /**
     * 모든 엔티티 조회 (로컬 우선, 백그라운드 동기화)
     */
    fun getAll(): Flow<List<T>>
    
    /**
     * ID로 엔티티 조회
     */
    suspend fun getById(id: Any): T?
    
    /**
     * 엔티티 생성 (로컬 즉시 반영, 백그라운드 동기화)
     */
    suspend fun create(entity: T): Result<T>
    
    /**
     * 엔티티 업데이트 (로컬 즉시 반영, 백그라운드 동기화)
     */
    suspend fun update(entity: T): Result<T>
    
    /**
     * 엔티티 삭제 (로컬 즉시 반영, 백그라운드 동기화)
     */
    suspend fun delete(id: Any): Result<Unit>
    
    /**
     * 강제 동기화 (수동 새로고침)
     */
    suspend fun forceSync(): Result<Unit>
    
    /**
     * 동기화 상태 조회
     */
    val syncStatus: Flow<SyncStatus>
}

// ====================================
// 구체적인 Repository 인터페이스들
// ====================================

interface BookRepository : SyncableRepository<BookEntity> {
    /**
     * 사용자의 모든 책 조회
     */
    fun getAllBooks(): Flow<List<BookEntity>>
    
    /**
     * 진행 중인 책들만 조회
     */
    fun getBooksInProgress(): Flow<List<BookEntity>>
    
    /**
     * 완독한 책들만 조회
     */
    fun getCompletedBooks(): Flow<List<BookEntity>>
    
    /**
     * 태그별 책 조회
     */
    fun getBooksByTag(tagId: Long): Flow<List<BookEntity>>
    
    /**
     * 독서 진행률 업데이트
     */
    suspend fun updateProgress(bookId: Int, currentPage: Int, elapsedSeconds: Int): Result<Unit>
}

interface MemoRepository : SyncableRepository<MemoEntity> {
    /**
     * 특정 책의 모든 메모
     */
    fun getMemosByBook(bookId: Int): Flow<List<MemoEntity>>
    
    /**
     * 페이지별 메모 조회
     */
    fun getMemosByPage(bookId: Int, pageNumber: Int): Flow<List<MemoEntity>>
    
    /**
     * 최근 메모들
     */
    fun getRecentMemos(limit: Int = 10): Flow<List<MemoEntity>>
    
    /**
     * 메모 검색
     */
    suspend fun searchMemos(query: String): List<MemoEntity>
}

interface TagRepository : SyncableRepository<TagEntity> {
    /**
     * 사용자의 모든 태그
     */
    fun getAllTags(): Flow<List<TagEntity>>
    
    /**
     * 이름으로 태그 검색
     */
    suspend fun findByName(name: String): TagEntity?
    
    /**
     * 자주 사용되는 태그들
     */
    fun getPopularTags(limit: Int = 10): Flow<List<TagEntity>>
}

interface BookTagRepository : SyncableRepository<BookTagCrossRef> {
    /**
     * 책에 태그 추가
     */
    suspend fun addTagToBook(bookId: Int, tagId: Long): Result<Unit>
    
    /**
     * 책에서 태그 제거
     */
    suspend fun removeTagFromBook(bookId: Int, tagId: Long): Result<Unit>
    
    /**
     * 책의 모든 태그 조회
     */
    fun getTagsForBook(bookId: Int): Flow<List<TagEntity>>
    
    /**
     * 태그가 적용된 모든 책 조회
     */
    fun getBooksForTag(tagId: Long): Flow<List<BookEntity>>
}

// ====================================
// 충돌 해결 인터페이스
// ====================================

interface ConflictResolver<T : SyncableEntity> {
    /**
     * 충돌 해결
     */
    suspend fun resolve(conflict: Conflict<T>): ConflictResolution
    
    /**
     * 데이터 병합
     */
    suspend fun merge(local: T, remote: T): T
    
    /**
     * 충돌 감지
     */
    suspend fun detectConflict(local: T?, remote: T?): Conflict<T>?
}

data class Conflict<T>(
    val entityType: EntityType,
    val localEntity: T?,
    val remoteEntity: T?,
    val conflictType: ConflictType,
    val detectedAt: Long = System.currentTimeMillis()
)

enum class ConflictType {
    MODIFY_MODIFY,    // 로컬 수정 + 원격 수정
    MODIFY_DELETE,    // 로컬 수정 + 원격 삭제
    DELETE_MODIFY,    // 로컬 삭제 + 원격 수정
    DELETE_DELETE     // 로컬 삭제 + 원격 삭제
}

enum class ConflictResolution {
    KEEP_LOCAL,       // 로컬 데이터 유지
    KEEP_REMOTE,      // 원격 데이터 유지  
    MERGE_DATA,       // 데이터 병합
    USER_DECISION     // 사용자 선택
}

// ====================================
// 네트워크 상태 관리
// ====================================

interface NetworkStatusManager {
    val isOnline: Flow<Boolean>
    val networkType: Flow<NetworkType>
    
    suspend fun isNetworkAvailable(): Boolean
    fun requiresWifiForSync(): Boolean
}

enum class NetworkType {
    WIFI, MOBILE, NONE
}

// ====================================
// 알림 및 UI 상호작용 인터페이스
// ====================================

interface SyncNotificationManager {
    /**
     * 동기화 진행 상황 알림
     */
    fun showSyncProgress(progress: SyncProgress)
    
    /**
     * 동기화 완료 알림
     */
    fun showSyncComplete(result: SyncResult)
    
    /**
     * 충돌 발생 알림
     */
    fun showConflictNotification(conflicts: List<Conflict<*>>)
    
    /**
     * 오프라인 모드 알림
     */
    fun showOfflineMode()
    
    /**
     * 동기화 오류 알림
     */
    fun showSyncError(error: Throwable)
}

interface ConflictUIManager {
    /**
     * 충돌 해결 UI 표시
     */
    suspend fun showConflictResolutionDialog(conflict: Conflict<*>): ConflictResolution
    
    /**
     * 데이터 병합 UI 표시
     */
    suspend fun showMergeDataDialog(local: Any, remote: Any): Any
    
    /**
     * 충돌 해결 결과 표시
     */
    fun showResolutionResult(resolution: ConflictResolution)
}

// ====================================
// 로깅 및 분석 인터페이스
// ====================================

interface SyncLogger {
    /**
     * 동기화 이벤트 로그
     */
    suspend fun logSyncEvent(
        entityType: EntityType,
        operation: SyncOperation,
        result: SyncResult,
        duration: Long
    )
    
    /**
     * 충돌 로그
     */
    suspend fun logConflict(conflict: Conflict<*>, resolution: ConflictResolution)
    
    /**
     * 성능 메트릭 로그
     */
    suspend fun logPerformanceMetrics(metrics: SyncPerformanceMetrics)
    
    /**
     * 동기화 통계 조회
     */
    suspend fun getSyncStatistics(): SyncStatistics
}

enum class SyncOperation {
    INITIAL_SYNC, PUSH, PULL, CONFLICT_RESOLUTION
}

data class SyncPerformanceMetrics(
    val operation: SyncOperation,
    val entityType: EntityType,
    val itemCount: Int,
    val duration: Long,
    val networkTime: Long,
    val databaseTime: Long
)

data class SyncStatistics(
    val totalSyncs: Int,
    val successRate: Float,
    val averageDuration: Long,
    val conflictRate: Float,
    val lastSyncTime: Long
)