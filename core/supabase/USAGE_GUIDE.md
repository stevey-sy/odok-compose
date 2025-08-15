# Supabase 통합 사용 가이드

이 문서는 오독오독 앱에서 Supabase 통합을 사용하는 방법을 설명합니다.

## 1. 설정

### Supabase 프로젝트 설정

1. **Supabase 프로젝트 생성**
   - [Supabase Dashboard](https://app.supabase.com)에서 새 프로젝트 생성
   - 프로젝트 URL과 anon public key 확인

2. **설정 업데이트**
   ```kotlin
   // SupabaseConfig.kt 파일에서 실제 값으로 교체
   fun getDefault(): SupabaseConfig {
       return SupabaseConfig(
           url = "https://your-project.supabase.co",
           anonKey = "your-anon-key"
       )
   }
   ```

3. **RLS 정책 적용**
   - `RLS_POLICIES.md`의 SQL을 Supabase SQL 에디터에서 실행

## 2. 인증 사용법

### Google 로그인

```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: SupabaseAuthService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()
    
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            when (val result = authService.signInWithGoogle(idToken)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = result.data,
                        error = null
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is AuthResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }
}
```

### 인증 상태 관찰

```kotlin
@HiltViewModel 
class MainViewModel @Inject constructor(
    private val authService: SupabaseAuthService
) : ViewModel() {
    
    val currentUser = authService.currentUser
    val isAuthenticated = authService.isAuthenticated
    
    fun signOut() {
        viewModelScope.launch {
            authService.signOut()
        }
    }
}
```

## 3. 데이터 동기화 사용법

### 전체 동기화

```kotlin
@HiltViewModel
class SyncViewModel @Inject constructor(
    private val syncService: SupabaseSyncService,
    private val authService: SupabaseAuthService
) : ViewModel() {
    
    val syncStatus = syncService.syncStatus
    
    fun startSync() {
        viewModelScope.launch {
            val currentUser = authService.getCurrentUser()
            currentUser?.let { user ->
                syncService.syncAll(user.id)
            }
        }
    }
}
```

### 자동 동기화

```kotlin
@HiltViewModel
class BookViewModel @Inject constructor(
    private val syncService: SupabaseSyncService,
    private val authService: SupabaseAuthService
) : ViewModel() {
    
    init {
        // 앱 시작 시 동기화 필요 여부 확인
        viewModelScope.launch {
            val currentUser = authService.getCurrentUser()
            currentUser?.let { user ->
                if (syncService.needsSync(user.id)) {
                    syncService.syncAll(user.id)
                }
            }
        }
    }
}
```

## 4. Repository 패턴 통합

기존 Repository를 Supabase와 통합하는 방법:

### BookShelfRepository 업데이트

```kotlin
@Singleton
class BookShelfRepositoryImpl @Inject constructor(
    private val bookLocalDataSource: BookLocalDataSource,
    private val supabaseAuthService: SupabaseAuthService,
    private val supabaseSyncService: SupabaseSyncService
) : BookShelfRepository {
    
    override fun getMyBooks(): Flow<List<BookUiModel>> {
        return bookLocalDataSource.getAllBooks().map { entities ->
            entities.map { it.toUiModel() }
        }
    }
    
    override suspend fun saveBook(book: BookUiModel): Result<Unit> {
        return try {
            // 1. 로컬에 저장
            val entity = book.toEntity()
            bookLocalDataSource.insertBook(entity)
            
            // 2. 백그라운드에서 동기화
            val currentUser = supabaseAuthService.getCurrentUser()
            currentUser?.let { user ->
                // 논블로킹으로 동기화 실행
                CoroutineScope(Dispatchers.IO).launch {
                    supabaseSyncService.syncBooks(user.id)
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateBook(book: BookUiModel): Result<Unit> {
        return try {
            // 1. 로컬 업데이트
            val entity = book.toEntity()
            bookLocalDataSource.updateBook(entity)
            
            // 2. 백그라운드 동기화
            val currentUser = supabaseAuthService.getCurrentUser()
            currentUser?.let { user ->
                CoroutineScope(Dispatchers.IO).launch {
                    supabaseSyncService.syncBooks(user.id)
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

## 5. UI 통합

### 동기화 상태 표시

```kotlin
@Composable
fun SyncStatusIndicator(
    syncStatus: SyncResult<SyncStats>
) {
    when (syncStatus) {
        is SyncResult.Progress -> {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    progress = syncStatus.progress,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("동기화 중... ${(syncStatus.progress * 100).toInt()}%")
            }
        }
        is SyncResult.Success -> {
            val stats = syncStatus.data
            Text("동기화 완료: ${stats.syncedItems}/${stats.totalItems}")
        }
        is SyncResult.Error -> {
            Text(
                text = "동기화 실패: ${syncStatus.message}",
                color = MaterialTheme.colors.error
            )
        }
    }
}
```

### 로그인 화면

```kotlin
@Composable
fun LoginScreen(
    onGoogleSignIn: (String) -> Unit
) {
    val context = LocalContext.current
    
    GoogleSignInButton(
        onClick = {
            // Google Sign-In 로직
            // 성공 시 onGoogleSignIn(idToken) 호출
        }
    )
}
```

## 6. 오프라인 지원

### 네트워크 상태 확인

```kotlin
@Singleton
class NetworkConnectivityObserver @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    val isConnected: Flow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }
            
            override fun onLost(network: Network) {
                trySend(false)
            }
        }
        
        connectivityManager.registerDefaultNetworkCallback(callback)
        
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
}
```

### 오프라인 우선 Repository

```kotlin
@Singleton 
class OfflineFirstBookRepository @Inject constructor(
    private val bookDao: BookDao,
    private val supabaseBookRepository: SupabaseBookRepository,
    private val syncService: SupabaseSyncService,
    private val connectivityObserver: NetworkConnectivityObserver
) {
    
    fun getBooks(userId: String): Flow<List<BookEntity>> {
        return combine(
            bookDao.getAllBooks(userId),
            connectivityObserver.isConnected
        ) { localBooks, isConnected ->
            
            if (isConnected && localBooks.isEmpty()) {
                // 온라인 상태이고 로컬 데이터가 없으면 동기화 시도
                syncService.pullRemoteChanges(userId)
            }
            
            localBooks
        }
    }
}
```

## 7. 에러 처리

### 글로벌 에러 핸들링

```kotlin
@Composable
fun AppContent() {
    val syncViewModel: SyncViewModel = hiltViewModel()
    val syncStatus by syncViewModel.syncStatus.collectAsState()
    
    LaunchedEffect(syncStatus) {
        if (syncStatus is SyncResult.Error) {
            // 에러 토스트 표시
            // 또는 에러 다이얼로그 표시
        }
    }
}
```

## 8. 성능 최적화

### 배치 동기화

```kotlin
class BatchSyncService @Inject constructor(
    private val syncService: SupabaseSyncService
) {
    
    private val syncQueue = mutableListOf<String>()
    private var syncJob: Job? = null
    
    fun requestSync(userId: String) {
        syncQueue.add(userId)
        
        syncJob?.cancel()
        syncJob = CoroutineScope(Dispatchers.IO).launch {
            delay(1000) // 1초 디바운스
            
            val uniqueUserIds = syncQueue.toSet()
            syncQueue.clear()
            
            uniqueUserIds.forEach { userId ->
                syncService.syncAll(userId)
            }
        }
    }
}
```

## 9. 테스트

### Repository 테스트

```kotlin
@RunWith(AndroidJUnit4::class)
class BookRepositoryTest {
    
    @Mock
    private lateinit var mockSyncService: SupabaseSyncService
    
    @Mock
    private lateinit var mockAuthService: SupabaseAuthService
    
    @Test
    fun testSaveBook_syncsCalls() = runTest {
        // Given
        val book = BookUiModel(...)
        val user = AuthUser(id = "test-user", ...)
        
        whenever(mockAuthService.getCurrentUser()).thenReturn(user)
        
        // When
        repository.saveBook(book)
        
        // Then
        verify(mockSyncService).syncBooks(user.id)
    }
}
```

이 가이드를 따르면 Supabase가 완전히 통합된 오프라인 우선 앱을 구현할 수 있습니다.