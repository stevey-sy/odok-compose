# Flow, StateFlow, MutableStateFlow 완전 정복 가이드

## 🚰 물의 흐름으로 이해하기

### 1. **Flow<T>** = 일반 수도꼭지
```kotlin
// 데이터가 흘러나오는 기본 통로
flow { 
    emit("데이터1")
    emit("데이터2") 
}
```
- 누군가 collect할 때만 데이터 흘러나옴
- 매번 새로 시작됨 (Cold Stream)

### 2. **StateFlow<T>** = 저수조가 있는 수도꼭지  
```kotlin
// 항상 최신 값을 보관하고 있음
val stateFlow = MutableStateFlow("초기값")
```
- 항상 현재 값(state)을 가지고 있음
- 구독하면 즉시 현재 값을 받음 (Hot Stream)
- UI 상태 관리에 perfect!

### 3. **MutableStateFlow<T>** = 조절 가능한 저수조
```kotlin
val mutableState = MutableStateFlow("초기값")
mutableState.value = "새값"  // 값 변경 가능
```
- StateFlow + 값 변경 기능
- 내부에서만 변경하고, 외부에는 읽기전용으로 노출

## 🔄 실제 사용 패턴

### 패턴 1: 기본 상태 관리
```kotlin
class MyViewModel : ViewModel() {
    // 내부에서는 변경 가능
    private val _uiState = MutableStateFlow(UiState.Loading)
    
    // 외부에는 읽기전용으로 노출
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    fun updateState() {
        _uiState.value = UiState.Success
    }
}
```

### 패턴 2: 여러 데이터 합치기 (combine)
```kotlin
// 두 개의 데이터 소스를 합침
val combinedState = combine(
    userFlow,           // Flow<User>
    settingsFlow        // Flow<Settings>
) { user, settings ->
    UiState(user, settings)  // 합쳐진 결과
}.stateIn(...)  // StateFlow로 변환
```

## 🎯 언제 뭘 쓸까?

### Flow<T> 쓰는 경우
```kotlin
// 일회성 작업, API 호출
suspend fun fetchData(): Flow<Data> = flow {
    val data = api.getData()
    emit(data)
}
```

### StateFlow<T> 쓰는 경우  
```kotlin
// UI 상태, 항상 최신값 필요
@Composable
fun MyScreen(uiState: StateFlow<UiState>) {
    val state by uiState.collectAsState()
    // state는 항상 최신값
}
```

### combine 쓰는 경우
```kotlin
// 여러 상태를 하나로 합칠 때
val finalState = combine(
    loginState,
    dataState
) { login, data ->
    when {
        !login -> UiState.NotLoggedIn
        data.isEmpty() -> UiState.Empty  
        else -> UiState.Success(data)
    }
}
```

## 🤔 헷갈리는 부분 해결

### Q: asStateFlow() vs stateIn() 언제 쓰나요?
```kotlin
// asStateFlow() - MutableStateFlow → StateFlow 변환
private val _state = MutableStateFlow(초기값)
val state = _state.asStateFlow()  ✅

// stateIn() - Flow → StateFlow 변환  
val state = someFlow.stateIn(...)  ✅

// 이건 안됨!
val state = combine(...).asStateFlow()  ❌
```

### Q: 실무에서는 어떻게 쓰나요?
```kotlin
class RealViewModel : ViewModel() {
    // 1. 간단한 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    // 2. 복잡한 조합 상태
    val uiState = combine(
        loginRepository.isLoggedIn(),
        dataRepository.getData()
    ) { loggedIn, data ->
        // 로직...
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Loading
    )
}
```

## 💡 학습 팁

1. **StateFlow = 항상 값이 있는 상자** 라고 생각하세요
2. **combine = 여러 상자의 내용물을 섞기** 라고 생각하세요  
3. **실제 코드를 많이 써보세요** - 개념보다 패턴이 중요해요

## 🚨 자주 하는 실수

### 실수 1: Flow와 StateFlow 변환 헷갈리기
```kotlin
// ❌ 잘못된 방법
val state = combine(...).asStateFlow()

// ✅ 올바른 방법
val state = combine(...).stateIn(scope, started, initialValue)
```

### 실수 2: StateFlow 초기값 설정 안하기
```kotlin
// ❌ 초기값이 없음
val state = dataFlow.stateIn(scope, started)

// ✅ 초기값 설정
val state = dataFlow.stateIn(scope, started, initialValue = Loading)
```

### 실수 3: MutableStateFlow 외부 노출
```kotlin
// ❌ 외부에서 직접 변경 가능
val uiState = MutableStateFlow(Loading)

// ✅ 읽기전용으로 노출
private val _uiState = MutableStateFlow(Loading)
val uiState = _uiState.asStateFlow()
```

## 📚 오독오독 프로젝트 실제 적용 사례

### MainViewModel에서의 활용
```kotlin
@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _isDataLoading = MutableStateFlow(true)
    
    // combine으로 여러 상태를 합쳐서 최종 UI 상태 결정
    val uiState: StateFlow<MainUiState> = combine(
        authRepository.getLoginStatusFlow(),  // 로그인 상태
        _isDataLoading                        // 데이터 로딩 상태
    ) { isLoggedIn, isLoading ->
        when {
            isLoading -> MainUiState.Loading
            !isLoggedIn -> MainUiState.NotLoggedIn
            else -> MainUiState.Success
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState.Loading
    )
}
```

### UserPreferences에서의 활용
```kotlin
@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val _loginStatusFlow = MutableStateFlow(isUserLoggedIn())
    val loginStatusFlow: Flow<Boolean> = _loginStatusFlow.asStateFlow()
    
    fun setUserLoggedIn(isLoggedIn: Boolean) {
        // SharedPreferences 저장
        sharedPreferences.edit()
            .putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
            .apply()
        
        // Flow 업데이트
        _loginStatusFlow.value = isLoggedIn
    }
}
```

## 🔍 Cold Stream vs Hot Stream 심화 이해

### Cold Stream (Flow) - 일반 수도꼭지
- **API 호출**에 주로 사용 ✅
- **일회성 작업** (파일 읽기, DB 쿼리)
- **Room Database 쿼리** (Room이 자동으로 생성)
- 매번 collect할 때마다 새로운 작업 시작

```kotlin
// API 호출 예시
fun fetchUserData(userId: String): Flow<User> = flow {
    val user = api.getUser(userId)  // 매번 새로운 API 호출
    emit(user)
}

// Repository 패턴
interface UserRepository {
    suspend fun fetchUser(id: String): Flow<User>  // Cold Stream
    val currentUser: StateFlow<User?>              // Hot Stream  
}
```

### Hot Stream (StateFlow) - 저수조가 있는 수도꼭지

#### 🚰 저수조 비유가 완벽한 이유
```kotlin
val loginState = MutableStateFlow(false)  // 저수조에 false 저장

// 👀 언제든지 현재 상태 확인 가능 (저수조 확인)
println(loginState.value)  // 즉시 현재 값 출력

// 🔄 상태 변경 (저수조 내용물 교체)
loginState.value = true

// 👥 새로운 구독자가 와도 즉시 현재 값 받음
loginState.collect { isLoggedIn ->
    println("현재 상태: $isLoggedIn")  // 즉시 true 출력
}
```

#### StateFlow vs Flow의 결정적 차이
```kotlin
// StateFlow (저수조 있음)
val stateFlow = MutableStateFlow("초기값")
println(stateFlow.value)  // ✅ 즉시 "초기값" 출력

// Flow (저수조 없음)  
val flow = flow { emit("데이터") }
// flow.value  // ❌ 컴파일 에러! 현재 값 알 수 없음
```

## 🔄 combine 완전 정복

### combine의 진짜 용도 = **여러 Flow 합치기**
```kotlin
// 두 개의 독립적인 Flow를 하나로 합치기
val combinedFlow = combine(
    userFlow,      // Flow<User>
    settingsFlow   // Flow<Settings>
) { user, settings ->
    UiState(user = user, settings = settings)  // 합쳐진 결과
}
```

### StateFlow 변환은 추가 옵션 (UI용)
```kotlin
// Option 1: Flow로 사용 (Cold Stream)
val combinedFlow = combine(userFlow, settingsFlow) { user, settings ->
    UiState(user, settings)
}

// Option 2: StateFlow로 변환 (Hot Stream) - UI용
val uiState = combine(userFlow, settingsFlow) { user, settings ->
    UiState(user, settings)
}.stateIn(...)  // UI에서 편하게 쓰려고 하는 추가 처리
```

### 실무 combine 패턴들
```kotlin
// 패턴 1: 로그인 + 로딩 + 데이터 상태 조합
val uiState = combine(
    authRepository.isLoggedIn(),  // Flow<Boolean>
    dataRepository.isLoading(),   // Flow<Boolean>
    dataRepository.getData()      // Flow<List<Data>>
) { isLoggedIn, isLoading, data ->
    when {
        !isLoggedIn -> UiState.NotLoggedIn
        isLoading -> UiState.Loading
        data.isEmpty() -> UiState.Empty
        else -> UiState.Success(data)
    }
}

// 패턴 2: 검색 + 필터 + 정렬 조합
val searchResults = combine(
    searchQueryFlow,    // 검색어
    filterFlow,         // 필터 조건
    sortOrderFlow       // 정렬 순서
) { query, filter, sort ->
    repository.search(query, filter, sort)
}.flatMapLatest { it }
```

## 🚀 flatMapLatest 완전 정복

### flatMapLatest = "가장 최신 것만 살려두고 나머지 취소"

```kotlin
// 사용자가 빠르게 프로필 전환: User1 → User2 → User3
val userPosts = userFlow.flatMapLatest { user ->
    postsRepository.getPostsByUser(user.id)  // 새로운 Flow 생성
}

// 동작 과정:
// User1 클릭 → API 호출 시작
// User2 클릭 → User1 API 취소, User2 API 시작  
// User3 클릭 → User2 API 취소, User3 API 시작
// 결과: User3의 데이터만 받음 ✅
```

### 다른 연산자들과의 차이점
```kotlin
// flatMapLatest (가장 최신만) - 검색, 사용자 전환에 적합
userFlow.flatMapLatest { user ->
    postsRepository.getPostsByUser(user.id)
}
// 결과: User3의 포스트만 (이전 것들 취소)

// flatMapConcat (순서대로 기다림) - 순서가 중요할 때
userFlow.flatMapConcat { user ->
    postsRepository.getPostsByUser(user.id)
}  
// 결과: User1 → User2 → User3 순서대로 모든 결과

// flatMapMerge (전부 동시에) - 병렬 처리할 때
userFlow.flatMapMerge { user ->
    postsRepository.getPostsByUser(user.id)
}
// 결과: User1, User2, User3 결과가 뒤섞여서 도착
```

### 실무 flatMapLatest 활용
```kotlin
// 1. 검색 기능 (가장 일반적)
val searchResults = searchQueryFlow.flatMapLatest { query ->
    if (query.isBlank()) {
        flowOf(emptyList())
    } else {
        searchRepository.search(query)  // 이전 검색 자동 취소
    }
}

// 2. 사용자 프로필 전환
val currentUserPosts = selectedUserFlow.flatMapLatest { user ->
    postsRepository.getPostsByUserId(user.id)
}

// 3. 필터 변경
val filteredData = filterFlow.flatMapLatest { filter ->
    dataRepository.getData(filter)  // 이전 필터 결과 취소
}
```

## 🆚 combine vs flatMapLatest 언제 쓸까?

### combine 쓰는 경우: **여러 데이터가 모두 필요**
```kotlin
val profileScreen = combine(
    userFlow,     // 사용자 정보
    friendsFlow,  // 친구 목록  
    postsFlow     // 게시물 목록
) { user, friends, posts ->
    ProfileScreenState(user, friends, posts)  // 셋 다 필요
}
```

### flatMapLatest 쓰는 경우: **하나가 다른 것을 트리거**
```kotlin  
val userPosts = userFlow.flatMapLatest { user ->
    postsRepository.getPostsByUser(user.id)  // 사용자 변경 → 새로운 포스트 조회
}
```

## 🎯 복습 체크리스트

- [ ] Flow는 Cold Stream (API 호출용), StateFlow는 Hot Stream (상태 관리용)
- [ ] StateFlow = 저수조 있는 수도꼭지 (항상 현재 값 저장)
- [ ] MutableStateFlow는 내부용, StateFlow는 외부 노출용
- [ ] combine = 여러 Flow 합치기 (StateFlow 변환은 추가 옵션)
- [ ] combine 결과는 stateIn()으로 StateFlow 변환
- [ ] flatMapLatest = 가장 최신 것만 유지, 이전 것들 취소
- [ ] 검색, 사용자 전환, 필터 변경에는 flatMapLatest 사용
- [ ] SharingStarted.WhileSubscribed(5000) 의미 이해
- [ ] "Latest"는 가장 마지막 데이터가 아닌 가장 최신 Flow 유지