# Supabase ↔ Room DB 매핑 가이드

## 테이블 매핑

### 1. UserEntity ↔ auth.users + profiles

**Room (UserEntity)**
```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String, // Supabase UUID
    val email: String,
    val displayName: String? = null,
    val profileImageUrl: String? = null,
    val provider: String = "google",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastSyncAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val preferredLanguage: String = "ko",
    val timezone: String = "Asia/Seoul"
)
```

**Supabase (auth.users + profiles)**
```sql
-- auth.users (자동 생성)
id: uuid
email: varchar
created_at: timestamptz
updated_at: timestamptz

-- public.profiles (사용자 정의)
id: uuid → auth.users(id)
display_name: text
profile_image_url: text
provider: text
created_at: timestamptz
updated_at: timestamptz
last_sync_at: timestamptz
is_active: boolean
preferred_language: text
timezone: text
```

### 2. BookEntity ↔ books

**매핑:**
- `itemId` ↔ `item_id`
- `userId` ↔ `user_id`
- Room의 String 타입 → Supabase의 text 타입
- Room의 Int 타입 → Supabase의 integer 타입
- Room의 Float 타입 → Supabase의 real 타입

### 3. MemoEntity ↔ memos

**매핑:**
- `memoId` ↔ `memo_id`
- `userId` ↔ `user_id`
- `bookId` ↔ `book_id`
- Room의 Long 타입 (createdAt, updatedAt) → Supabase의 bigint 타입

### 4. TagEntity ↔ tags

**매핑:**
- `tagId` ↔ `tag_id`
- `userId` ↔ `user_id`
- Room의 Long 타입 → Supabase의 bigint 타입

### 5. BookTagCrossRef ↔ book_tag_cross_ref

**매핑:**
- `bookId` ↔ `book_id`
- `tagId` ↔ `tag_id`

## 데이터 타입 매핑

| Room (Kotlin) | Supabase (PostgreSQL) | 비고 |
|---------------|----------------------|------|
| String | text | |
| Int | integer | |
| Long | bigint | timestamp 값 |
| Float | real | 평점 등 |
| Boolean | boolean | |
| UUID String | uuid | 사용자 ID |

## 네이밍 컨벤션

**Room**: camelCase (`itemId`, `userId`, `createdAt`)
**Supabase**: snake_case (`item_id`, `user_id`, `created_at`)

## 동기화 시 주의사항

1. **ID 매핑**: Room은 auto-increment, Supabase는 serial 사용
2. **Timestamp**: Room은 milliseconds, Supabase는 timestamptz/bigint
3. **NULL 처리**: Room의 nullable 필드와 Supabase DEFAULT 값 일치 확인
4. **외래키**: Room ForeignKey와 Supabase REFERENCES 일치 확인

## RLS (Row Level Security) 적용

모든 테이블에 사용자별 데이터 격리 정책 적용:
- 조회: `auth.uid() = user_id`
- 삽입: `auth.uid() = user_id`
- 수정: `auth.uid() = user_id`
- 삭제: `auth.uid() = user_id`