// ====================================
// UUID 기반 동기화 로직 구현 예시
// ====================================

import kotlinx.serialization.Serializable
import java.util.UUID

// ====================================
// 1. 개선된 동기화 로직 - 단순화됨
// ====================================

class UUIDBasedSyncManager(
    private val localDataSource: BookDao,
    private val remoteDataSource: SupabaseBookDataSource
) {
    
    suspend fun syncBook(book: BookEntity): SyncResult {
        return try {
            // UUID가 동일하므로 단순 upsert 가능
            val syncedBook = remoteDataSource.upsert(book)
            
            // 로컬 DB에도 동일한 UUID로 업데이트
            localDataSource.upsert(
                syncedBook.copy(
                    syncStatus = SyncStatus.SYNCED,
                    lastSyncAt = System.currentTimeMillis()
                )
            )
            
            SyncResult.Success
        } catch (e: Exception) {
            SyncResult.Failure(e)
        }
    }
    
    suspend fun pullRemoteChanges(): SyncResult {
        return try {
            val userId = getCurrentUserId()
            val lastSyncTime = getLastSyncTime()
            
            // 마지막 동기화 이후 변경된 원격 데이터 조회
            val remoteBooks = remoteDataSource.getUpdatedSince(userId, lastSyncTime)
            
            // UUID가 같으므로 직접 upsert (충돌 해결 자동)
            remoteBooks.forEach { remoteBook ->
                val localBook = localDataSource.getById(remoteBook.itemId)
                
                when {
                    localBook == null -> {
                        // 새 원격 데이터 → 로컬에 삽입
                        localDataSource.insert(remoteBook)
                    }
                    
                    localBook.updatedAt < remoteBook.updatedAt -> {
                        // 원격이 더 최신 → 로컬 업데이트
                        localDataSource.update(remoteBook)
                    }
                    
                    localBook.updatedAt > remoteBook.updatedAt -> {
                        // 로컬이 더 최신 → 원격으로 푸시
                        remoteDataSource.upsert(localBook)
                    }
                    
                    // else: 동일한 시간 → 변경 없음
                }
            }
            
            SyncResult.Success
        } catch (e: Exception) {
            SyncResult.Failure(e)
        }
    }
}

// ====================================
// 2. Supabase 데이터소스 구현
// ====================================

@Serializable
data class SupabaseBook(
    val item_id: String,
    val user_id: String,
    val title: String,
    val author: String,
    val publisher: String,
    val isbn: String,
    val cover_image_url: String,
    val book_type: String = "",
    val total_page_cnt: Int = 0,
    val current_page_cnt: Int = 0,
    val challenge_page_cnt: Int = 0,
    val start_date: String = "",
    val end_date: String = "",
    val elapsed_time_in_seconds: Int = 0,
    val completed_reading_cnt: Int = 0,
    val description: String = "",
    val rate: Float = 0f,
    val finished_read_cnt: Int = 0,
    val created_at: Long,
    val updated_at: Long
)

class SupabaseBookDataSource(
    private val supabaseClient: SupabaseClient
) {
    
    suspend fun upsert(book: BookEntity): BookEntity {
        val supabaseBook = book.toSupabaseFormat()
        
        val result = supabaseClient
            .from("books")
            .upsert(supabaseBook) // UUID가 같으면 업데이트, 없으면 삽입
            .select()
            .decodeSingle<SupabaseBook>()
        
        return result.toRoomEntity()
    }
    
    suspend fun getById(bookId: String): BookEntity? {
        return try {
            val result = supabaseClient
                .from("books")
                .select()
                .eq("item_id", bookId)
                .maybeSingle<SupabaseBook>()
            
            result?.toRoomEntity()
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun getAllForUser(userId: String): List<BookEntity> {
        val results = supabaseClient
            .from("books")
            .select()
            .eq("user_id", userId)
            .decodeList<SupabaseBook>()
        
        return results.map { it.toRoomEntity() }
    }
    
    suspend fun getUpdatedSince(userId: String, timestamp: Long): List<BookEntity> {
        val results = supabaseClient
            .from("books")
            .select()
            .eq("user_id", userId)
            .gt("updated_at", timestamp) // timestamp 이후 수정된 것들만
            .decodeList<SupabaseBook>()
        
        return results.map { it.toRoomEntity() }
    }
    
    suspend fun delete(bookId: String) {
        supabaseClient
            .from("books")
            .delete()
            .eq("item_id", bookId)
    }
}

// ====================================
// 3. 데이터 변환 함수들
// ====================================

fun BookEntity.toSupabaseFormat(): SupabaseBook {
    return SupabaseBook(
        item_id = this.itemId,
        user_id = this.userId,
        title = this.title,
        author = this.author,
        publisher = this.publisher,
        isbn = this.isbn,
        cover_image_url = this.coverImageUrl,
        book_type = this.bookType,
        total_page_cnt = this.totalPageCnt,
        current_page_cnt = this.currentPageCnt,
        challenge_page_cnt = this.challengePageCnt,
        start_date = this.startDate,
        end_date = this.endDate,
        elapsed_time_in_seconds = this.elapsedTimeInSeconds,
        completed_reading_cnt = this.completedReadingCnt,
        description = this.description,
        rate = this.rate,
        finished_read_cnt = this.finishedReadCnt,
        created_at = this.createdAt,
        updated_at = this.updatedAt
    )
}

fun SupabaseBook.toRoomEntity(): BookEntity {
    return BookEntity(
        itemId = this.item_id,
        userId = this.user_id,
        title = this.title,
        author = this.author,
        publisher = this.publisher,
        isbn = this.isbn,
        coverImageUrl = this.cover_image_url,
        bookType = this.book_type,
        totalPageCnt = this.total_page_cnt,
        currentPageCnt = this.current_page_cnt,
        challengePageCnt = this.challenge_page_cnt,
        startDate = this.start_date,
        endDate = this.end_date,
        elapsedTimeInSeconds = this.elapsed_time_in_seconds,
        completedReadingCnt = this.completed_reading_cnt,
        description = this.description,
        rate = this.rate,
        finishedReadCnt = this.finished_read_cnt,
        createdAt = this.created_at,
        updatedAt = this.updated_at
    )
}

// ====================================
// 4. 개선된 Repository 구현
// ====================================

class UUIDBookRepository(
    private val localDataSource: BookDao,
    private val remoteDataSource: SupabaseBookDataSource,
    private val syncManager: SyncManager
) : BookRepository {
    
    override suspend fun create(book: BookEntity): Result<BookEntity> {
        return try {
            // UUID는 생성 시점에 자동으로 설정됨
            val newBook = if (book.itemId.isEmpty()) {
                book.copy(itemId = UUID.randomUUID().toString())
            } else {
                book
            }
            
            // 1. 로컬 DB에 즉시 저장
            localDataSource.insert(newBook)
            
            // 2. 백그라운드 동기화 큐에 추가
            syncManager.queueForSync(EntityType.BOOK, newBook.itemId)
            
            Result.success(newBook)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun update(book: BookEntity): Result<BookEntity> {
        return try {
            val updatedBook = book.copy(
                updatedAt = System.currentTimeMillis()
            )
            
            // 1. 로컬 DB 업데이트
            localDataSource.update(updatedBook)
            
            // 2. 동기화 큐에 추가
            syncManager.queueForSync(EntityType.BOOK, updatedBook.itemId)
            
            Result.success(updatedBook)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun delete(id: Any): Result<Unit> {
        return try {
            val bookId = id as String
            
            // 1. 로컬에서 삭제
            localDataSource.deleteById(bookId)
            
            // 2. 원격에서도 삭제하도록 큐에 추가
            syncManager.queueForDeletion(EntityType.BOOK, bookId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getById(id: Any): BookEntity? {
        val bookId = id as String
        return localDataSource.getById(bookId)
    }
    
    override fun getAllBooks(): Flow<List<BookEntity>> {
        return localDataSource.getAllBooks()
            .onStart {
                // 백그라운드에서 동기화 시도
                tryBackgroundSync()
            }
    }
    
    private fun tryBackgroundSync() {
        CoroutineScope(Dispatchers.IO).launch {
            syncManager.syncEntity(EntityType.BOOK)
        }
    }
}

// ====================================
// 5. 동기화 큐 관리 (UUID 버전)
// ====================================

@Entity(tableName = "sync_queue")
data class SyncQueueItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val entityType: String,        // "BOOK", "MEMO", "TAG"
    val entityId: String,          // UUID
    val operation: String,         // "UPSERT", "DELETE"
    val priority: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val retryCount: Int = 0,
    val lastAttempt: Long = 0,
    val data: String? = null       // JSON 데이터
)

class UUIDSyncQueueManager(
    private val syncQueueDao: SyncQueueDao,
    private val bookDataSource: SupabaseBookDataSource,
    private val memoDataSource: SupabaseMemoDataSource,
    private val tagDataSource: SupabaseTagDataSource
) {
    
    suspend fun processQueue() {
        val pendingItems = syncQueueDao.getAllPendingItems()
        
        pendingItems.forEach { queueItem ->
            try {
                when (queueItem.entityType to queueItem.operation) {
                    "BOOK" to "UPSERT" -> {
                        val book = Json.decodeFromString<BookEntity>(queueItem.data!!)
                        bookDataSource.upsert(book)
                    }
                    
                    "BOOK" to "DELETE" -> {
                        bookDataSource.delete(queueItem.entityId)
                    }
                    
                    "MEMO" to "UPSERT" -> {
                        val memo = Json.decodeFromString<MemoEntity>(queueItem.data!!)
                        memoDataSource.upsert(memo)
                    }
                    
                    "MEMO" to "DELETE" -> {
                        memoDataSource.delete(queueItem.entityId)
                    }
                    
                    "TAG" to "UPSERT" -> {
                        val tag = Json.decodeFromString<TagEntity>(queueItem.data!!)
                        tagDataSource.upsert(tag)
                    }
                    
                    "TAG" to "DELETE" -> {
                        tagDataSource.delete(queueItem.entityId)
                    }
                }
                
                // 성공 시 큐에서 제거
                syncQueueDao.delete(queueItem)
                
            } catch (e: Exception) {
                // 실패 시 재시도 카운터 증가
                if (queueItem.retryCount < 3) {
                    syncQueueDao.updateRetryInfo(
                        queueItem.id,
                        queueItem.retryCount + 1,
                        System.currentTimeMillis()
                    )
                } else {
                    // 최대 재시도 초과 시 에러 로그 후 제거
                    Log.e("Sync", "Max retries exceeded for ${queueItem.entityId}")
                    syncQueueDao.delete(queueItem)
                }
            }
        }
    }
}

// ====================================
// 6. 실시간 동기화 (UUID 버전)
// ====================================

class UUIDRealtimeSyncManager(
    private val supabaseClient: SupabaseClient,
    private val localDataSource: BookDao
) {
    
    private var realtimeChannel: RealtimeChannel? = null
    
    fun startRealTimeSync() {
        val userId = getCurrentUserId()
        
        realtimeChannel = supabaseClient.channel("books-$userId") {
            
            postgresChanges(
                event = PostgresAction.All,
                schema = "public",
                table = "books",
                filter = "user_id=eq.$userId"
            ) { payload ->
                handleBookChange(payload)
            }
        }
        
        realtimeChannel?.subscribe()
    }
    
    private suspend fun handleBookChange(payload: PostgresAction) {
        when (payload) {
            is PostgresAction.Insert -> {
                val supabaseBook = payload.decodeRecord<SupabaseBook>()
                val roomBook = supabaseBook.toRoomEntity()
                
                // UUID로 upsert (충돌 시 자동 해결)
                localDataSource.upsert(roomBook)
            }
            
            is PostgresAction.Update -> {
                val supabaseBook = payload.decodeRecord<SupabaseBook>()
                val roomBook = supabaseBook.toRoomEntity()
                
                localDataSource.upsert(roomBook)
            }
            
            is PostgresAction.Delete -> {
                val bookId = payload.oldRecord["item_id"] as String
                localDataSource.deleteById(bookId)
            }
        }
    }
}

// ====================================
// 7. UUID 기반 DAO 메서드들
// ====================================

@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<BookEntity>>
    
    @Query("SELECT * FROM books WHERE itemId = :id")
    suspend fun getById(id: String): BookEntity?
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(book: BookEntity): Long
    
    @Update
    suspend fun update(book: BookEntity)
    
    @Query("DELETE FROM books WHERE itemId = :id")
    suspend fun deleteById(id: String)
    
    // UUID에서는 upsert가 매우 유용
    @Transaction
    suspend fun upsert(book: BookEntity) {
        val existingBook = getById(book.itemId)
        if (existingBook == null) {
            insert(book)
        } else {
            update(book)
        }
    }
    
    @Query("SELECT * FROM books WHERE userId = :userId AND updatedAt > :timestamp")
    suspend fun getUpdatedSince(userId: String, timestamp: Long): List<BookEntity>
}

// ====================================
// 8. 마이그레이션 전략
// ====================================

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 기존 Int ID를 UUID로 변환하는 마이그레이션
        
        // 1. 새 테이블 생성 (UUID 사용)
        database.execSQL("""
            CREATE TABLE books_new (
                itemId TEXT PRIMARY KEY NOT NULL,
                userId TEXT NOT NULL,
                title TEXT NOT NULL,
                author TEXT NOT NULL,
                publisher TEXT NOT NULL,
                isbn TEXT NOT NULL,
                coverImageUrl TEXT NOT NULL,
                bookType TEXT NOT NULL DEFAULT '',
                totalPageCnt INTEGER NOT NULL DEFAULT 0,
                currentPageCnt INTEGER NOT NULL DEFAULT 0,
                challengePageCnt INTEGER NOT NULL DEFAULT 0,
                startDate TEXT NOT NULL DEFAULT '',
                endDate TEXT NOT NULL DEFAULT '',
                elapsedTimeInSeconds INTEGER NOT NULL DEFAULT 0,
                completedReadingCnt INTEGER NOT NULL DEFAULT 0,
                description TEXT NOT NULL DEFAULT '',
                rate REAL NOT NULL DEFAULT 0.0,
                finishedReadCnt INTEGER NOT NULL DEFAULT 0,
                createdAt INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL
            )
        """)
        
        // 2. 기존 데이터를 새 UUID와 함께 복사
        database.execSQL("""
            INSERT INTO books_new (
                itemId, userId, title, author, publisher, isbn, coverImageUrl,
                bookType, totalPageCnt, currentPageCnt, challengePageCnt,
                startDate, endDate, elapsedTimeInSeconds, completedReadingCnt,
                description, rate, finishedReadCnt, createdAt, updatedAt
            )
            SELECT 
                lower(hex(randomblob(4))) || '-' || lower(hex(randomblob(2))) || '-' || 
                lower(hex(randomblob(2))) || '-' || lower(hex(randomblob(2))) || '-' || 
                lower(hex(randomblob(6))) as itemId,
                userId, title, author, publisher, isbn, coverImageUrl,
                bookType, totalPageCnt, currentPageCnt, challengePageCnt,
                startDate, endDate, elapsedTimeInSeconds, completedReadingCnt,
                description, rate, finishedReadCnt,
                strftime('%s', 'now') * 1000 as createdAt,
                strftime('%s', 'now') * 1000 as updatedAt
            FROM books
        """)
        
        // 3. 기존 테이블 삭제 후 새 테이블로 교체
        database.execSQL("DROP TABLE books")
        database.execSQL("ALTER TABLE books_new RENAME TO books")
    }
}