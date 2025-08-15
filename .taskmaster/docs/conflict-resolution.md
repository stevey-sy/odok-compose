# 데이터 충돌 해결 전략

## 충돌 발생 시나리오

### 1. 일반적인 충돌 상황
```
Local Device A     Remote (Supabase)     Local Device B
      |                   |                   |
   Update Book         Update Book        Update Book
   (Title: "새책")     (Title: "New Book") (Author: "저자")
      |                   |                   |
      └─── Sync ──────────┼────── Sync ───────┘
                          |
                   CONFLICT!
```

### 2. 충돌 유형 정의

#### ConflictType 열거형
```kotlin
enum class ConflictType {
    MODIFY_MODIFY,    // 로컬 수정 + 원격 수정
    MODIFY_DELETE,    // 로컬 수정 + 원격 삭제  
    DELETE_MODIFY,    // 로컬 삭제 + 원격 수정
    DELETE_DELETE     // 로컬 삭제 + 원격 삭제 (충돌 아님)
}
```

#### Conflict 데이터 클래스
```kotlin
data class Conflict<T>(
    val entityType: EntityType,
    val localEntity: T?,
    val remoteEntity: T?,
    val conflictType: ConflictType,
    val detectedAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null,
    val resolution: ConflictResolution? = null
)

enum class ConflictResolution {
    KEEP_LOCAL,       // 로컬 데이터 유지
    KEEP_REMOTE,      // 원격 데이터 유지
    MERGE_DATA,       // 데이터 병합
    USER_DECISION     // 사용자 선택
}
```

## 핵심 충돌 해결 전략

### 1. Last-Write-Wins (기본 전략)

```kotlin
class LastWriteWinsResolver<T : SyncableEntity> : ConflictResolver<T> {
    
    override suspend fun resolve(conflict: Conflict<T>): ConflictResolution {
        val local = conflict.localEntity
        val remote = conflict.remoteEntity
        
        return when (conflict.conflictType) {
            ConflictType.MODIFY_MODIFY -> {
                // 더 최근에 수정된 데이터 우선
                if (local?.updatedAt ?: 0 > remote?.updatedAt ?: 0) {
                    ConflictResolution.KEEP_LOCAL
                } else {
                    ConflictResolution.KEEP_REMOTE
                }
            }
            
            ConflictType.DELETE_MODIFY -> {
                // 삭제보다 수정 우선 (데이터 복원)
                ConflictResolution.KEEP_REMOTE
            }
            
            ConflictType.MODIFY_DELETE -> {
                // 수정된 데이터 우선 (삭제 취소)
                ConflictResolution.KEEP_LOCAL
            }
            
            ConflictType.DELETE_DELETE -> {
                // 양쪽 모두 삭제 - 충돌 아님
                ConflictResolution.KEEP_REMOTE
            }
        }
    }
}
```

### 2. 엔티티별 특화 해결 전략

#### BookEntity 충돌 해결
```kotlin
class BookConflictResolver : ConflictResolver<BookEntity> {
    
    override suspend fun resolve(conflict: Conflict<BookEntity>): ConflictResolution {
        val local = conflict.localEntity
        val remote = conflict.remoteEntity
        
        return when (conflict.conflictType) {
            ConflictType.MODIFY_MODIFY -> {
                // 독서 진행률 관련 필드는 더 높은 값 우선
                if (shouldMergeBookData(local, remote)) {
                    ConflictResolution.MERGE_DATA
                } else {
                    // 일반적인 Last-Write-Wins
                    if (local?.updatedAt ?: 0 > remote?.updatedAt ?: 0) {
                        ConflictResolution.KEEP_LOCAL
                    } else {
                        ConflictResolution.KEEP_REMOTE
                    }
                }
            }
            else -> {
                // 기본 전략 사용
                super.resolve(conflict)
            }
        }
    }
    
    private fun shouldMergeBookData(
        local: BookEntity?,
        remote: BookEntity?
    ): Boolean {
        if (local == null || remote == null) return false
        
        // 독서 진행률이 다르면 병합 필요
        return local.currentPageCnt != remote.currentPageCnt ||
               local.elapsedTimeInSeconds != remote.elapsedTimeInSeconds
    }
    
    suspend fun mergeBookData(
        local: BookEntity,
        remote: BookEntity
    ): BookEntity {
        return BookEntity(
            itemId = local.itemId,
            userId = local.userId,
            title = if (local.updatedAt > remote.updatedAt) local.title else remote.title,
            author = if (local.updatedAt > remote.updatedAt) local.author else remote.author,
            // 진행률은 더 높은 값 사용
            currentPageCnt = maxOf(local.currentPageCnt, remote.currentPageCnt),
            elapsedTimeInSeconds = maxOf(local.elapsedTimeInSeconds, remote.elapsedTimeInSeconds),
            completedReadingCnt = maxOf(local.completedReadingCnt, remote.completedReadingCnt),
            // 메타데이터는 최신 수정 시간 기준
            description = if (local.updatedAt > remote.updatedAt) local.description else remote.description,
            rate = if (local.updatedAt > remote.updatedAt) local.rate else remote.rate,
            updatedAt = maxOf(local.updatedAt, remote.updatedAt)
        )
    }
}
```

#### MemoEntity 충돌 해결
```kotlin
class MemoConflictResolver : ConflictResolver<MemoEntity> {
    
    override suspend fun resolve(conflict: Conflict<MemoEntity>): ConflictResolution {
        return when (conflict.conflictType) {
            ConflictType.MODIFY_MODIFY -> {
                val local = conflict.localEntity!!
                val remote = conflict.remoteEntity!!
                
                // 내용이 완전히 다르면 사용자 선택 요청
                if (isContentDifferent(local, remote)) {
                    ConflictResolution.USER_DECISION
                } else {
                    // 내용이 비슷하면 Last-Write-Wins
                    if (local.updatedAt > remote.updatedAt) {
                        ConflictResolution.KEEP_LOCAL
                    } else {
                        ConflictResolution.KEEP_REMOTE
                    }
                }
            }
            else -> super.resolve(conflict)
        }
    }
    
    private fun isContentDifferent(local: MemoEntity, remote: MemoEntity): Boolean {
        val similarity = calculateSimilarity(local.content, remote.content)
        return similarity < 0.5 // 50% 미만 유사도면 사용자 선택
    }
    
    private fun calculateSimilarity(text1: String, text2: String): Double {
        // 단순한 유사도 계산 (실제로는 더 정교한 알고리즘 사용)
        val longer = if (text1.length > text2.length) text1 else text2
        val shorter = if (text1.length > text2.length) text2 else text1
        
        if (longer.isEmpty()) return 1.0
        
        val editDistance = levenshteinDistance(longer, shorter)
        return (longer.length - editDistance) / longer.length.toDouble()
    }
}
```

### 3. 사용자 개입 필요 시 UI 처리

#### ConflictResolutionDialog
```kotlin
@Composable
fun ConflictResolutionDialog(
    conflict: Conflict<*>,
    onResolved: (ConflictResolution) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text("데이터 충돌 해결") 
        },
        text = {
            Column {
                Text(
                    "같은 데이터가 다른 기기에서도 수정되었습니다.\n" +
                    "어떤 버전을 유지하시겠습니까?",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 로컬 데이터 미리보기
                ConflictDataPreview(
                    title = "이 기기에서 수정된 내용",
                    data = conflict.localEntity,
                    modifier = Modifier.background(
                        Color.Blue.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 원격 데이터 미리보기  
                ConflictDataPreview(
                    title = "다른 기기에서 수정된 내용",
                    data = conflict.remoteEntity,
                    modifier = Modifier.background(
                        Color.Green.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    )
                )
            }
        },
        confirmButton = {
            Row {
                TextButton(
                    onClick = { onResolved(ConflictResolution.KEEP_LOCAL) }
                ) {
                    Text("이 기기 버전 유지")
                }
                
                TextButton(
                    onClick = { onResolved(ConflictResolution.KEEP_REMOTE) }
                ) {
                    Text("다른 기기 버전 유지")
                }
                
                if (conflict.conflictType == ConflictType.MODIFY_MODIFY) {
                    TextButton(
                        onClick = { onResolved(ConflictResolution.MERGE_DATA) }
                    ) {
                        Text("병합")
                    }
                }
            }
        }
    )
}

@Composable
fun ConflictDataPreview(
    title: String,
    data: Any?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        when (data) {
            is BookEntity -> BookConflictPreview(data)
            is MemoEntity -> MemoConflictPreview(data)
            is TagEntity -> TagConflictPreview(data)
        }
    }
}
```

### 4. 충돌 해결 프로세스

#### ConflictResolutionManager
```kotlin
class ConflictResolutionManager(
    private val bookResolver: BookConflictResolver,
    private val memoResolver: MemoConflictResolver,
    private val tagResolver: TagConflictResolver,
    private val uiNotifier: ConflictUINotifier
) {
    
    suspend fun resolveConflicts(conflicts: List<Conflict<*>>): List<ResolvedConflict<*>> {
        val resolvedConflicts = mutableListOf<ResolvedConflict<*>>()
        
        for (conflict in conflicts) {
            val resolution = when (conflict.entityType) {
                EntityType.BOOK -> resolveBookConflict(conflict as Conflict<BookEntity>)
                EntityType.MEMO -> resolveMemoConflict(conflict as Conflict<MemoEntity>)  
                EntityType.TAG -> resolveTagConflict(conflict as Conflict<TagEntity>)
                EntityType.BOOK_TAG_CROSS_REF -> resolveSimpleConflict(conflict)
            }
            
            resolvedConflicts.add(resolution)
        }
        
        return resolvedConflicts
    }
    
    private suspend fun resolveBookConflict(
        conflict: Conflict<BookEntity>
    ): ResolvedConflict<BookEntity> {
        val resolution = bookResolver.resolve(conflict)
        
        return when (resolution) {
            ConflictResolution.USER_DECISION -> {
                // UI 다이얼로그 표시하고 사용자 선택 대기
                val userChoice = uiNotifier.showConflictDialog(conflict)
                applyResolution(conflict, userChoice)
            }
            ConflictResolution.MERGE_DATA -> {
                val mergedData = bookResolver.mergeBookData(
                    conflict.localEntity!!,
                    conflict.remoteEntity!!
                )
                ResolvedConflict(conflict, resolution, mergedData)
            }
            else -> applyResolution(conflict, resolution)
        }
    }
}
```

### 5. 충돌 로깅 및 분석

#### ConflictLogger
```kotlin
class ConflictLogger(
    private val database: AppDatabase
) {
    
    suspend fun logConflict(conflict: Conflict<*>, resolution: ConflictResolution) {
        val logEntry = ConflictLogEntity(
            entityType = conflict.entityType.name,
            conflictType = conflict.conflictType.name,
            resolution = resolution.name,
            occurredAt = conflict.detectedAt,
            resolvedAt = System.currentTimeMillis()
        )
        
        database.conflictLogDao().insert(logEntry)
    }
    
    suspend fun getConflictStatistics(): ConflictStatistics {
        val logs = database.conflictLogDao().getAllLogs()
        
        return ConflictStatistics(
            totalConflicts = logs.size,
            conflictsByType = logs.groupingBy { it.conflictType }.eachCount(),
            resolutionsByType = logs.groupingBy { it.resolution }.eachCount(),
            averageResolutionTime = logs.map { 
                it.resolvedAt - it.occurredAt 
            }.average()
        )
    }
}
```

## 충돌 방지 전략

### 1. 낙관적 잠금 (Optimistic Locking)
```kotlin
data class BookEntity(
    // ... 기존 필드들
    val version: Int = 1, // 버전 관리
    val lastModifiedBy: String? = null // 마지막 수정자
) {
    
    fun incrementVersion(): BookEntity {
        return this.copy(
            version = version + 1,
            updatedAt = System.currentTimeMillis()
        )
    }
}
```

### 2. 세분화된 타임스탬프
```kotlin
data class BookEntity(
    // ... 기존 필드들
    val titleUpdatedAt: Long = 0L,
    val progressUpdatedAt: Long = 0L,
    val ratingUpdatedAt: Long = 0L
) {
    
    fun updateTitle(newTitle: String): BookEntity {
        return this.copy(
            title = newTitle,
            titleUpdatedAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
    
    fun updateProgress(currentPage: Int): BookEntity {
        return this.copy(
            currentPageCnt = currentPage,
            progressUpdatedAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
}
```

### 3. 충돌 예측 및 경고
```kotlin
class ConflictPreventionManager {
    
    suspend fun checkPotentialConflicts(entity: SyncableEntity): ConflictRisk {
        val remoteVersion = fetchRemoteVersion(entity.id)
        val timeSinceLastSync = System.currentTimeMillis() - entity.lastSyncAt
        
        return when {
            remoteVersion > entity.version -> ConflictRisk.HIGH
            timeSinceLastSync > TimeUnit.HOURS.toMillis(1) -> ConflictRisk.MEDIUM
            else -> ConflictRisk.LOW
        }
    }
    
    fun showConflictWarning(risk: ConflictRisk) {
        when (risk) {
            ConflictRisk.HIGH -> {
                // "이 데이터가 다른 기기에서 수정되었을 수 있습니다"
                showWarningToast()
            }
            ConflictRisk.MEDIUM -> {
                // "동기화를 권장합니다"  
                showSyncSuggestion()
            }
            else -> { /* 경고 없음 */ }
        }
    }
}
```

## 테스트 전략

### 충돌 해결 테스트
```kotlin
@Test
fun `modify-modify conflict should use last write wins`() = runTest {
    // Given
    val localBook = createBookEntity(updatedAt = 1000L)
    val remoteBook = createBookEntity(updatedAt = 2000L)
    val conflict = Conflict(
        entityType = EntityType.BOOK,
        localEntity = localBook,
        remoteEntity = remoteBook,
        conflictType = ConflictType.MODIFY_MODIFY
    )
    
    // When
    val resolution = bookResolver.resolve(conflict)
    
    // Then
    assertEquals(ConflictResolution.KEEP_REMOTE, resolution)
}

@Test  
fun `book progress merge should keep higher values`() = runTest {
    // Given
    val localBook = createBookEntity(currentPageCnt = 100, elapsedTime = 3600)
    val remoteBook = createBookEntity(currentPageCnt = 80, elapsedTime = 4000)
    
    // When
    val merged = bookResolver.mergeBookData(localBook, remoteBook)
    
    // Then
    assertEquals(100, merged.currentPageCnt) // 로컬이 더 높음
    assertEquals(4000, merged.elapsedTimeInSeconds) // 원격이 더 높음
}
```