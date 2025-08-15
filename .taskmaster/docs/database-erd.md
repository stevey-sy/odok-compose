# Database ERD - Core Entities

## Entity Definitions

### UserEntity
```
Table: users
- userId (PK): String - Supabase UUID
- email: String
- displayName: String?
- profileImageUrl: String?
- provider: String = "google"
- createdAt: Long
- updatedAt: Long
- lastSyncAt: Long
- isActive: Boolean = true
- preferredLanguage: String = "ko"
- timezone: String = "Asia/Seoul"
```

### BookEntity (수정됨)
```
Table: books
- itemId (PK): Int (auto-generated)
- userId (FK): String → users.userId
- title: String
- author: String
- publisher: String
- isbn: String
- coverImageUrl: String
- bookType: String = ""
- totalPageCnt: Int = 0
- currentPageCnt: Int = 0
- challengePageCnt: Int = 0
- startDate: String = ""
- endDate: String = ""
- elapsedTimeInSeconds: Int = 0
- completedReadingCnt: Int = 0
- description: String = ""
- rate: Float = 0f
- finishedReadCnt: Int = 0
```

### MemoEntity (수정됨)
```
Table: memos
- memoId (PK): Int (auto-generated)
- userId (FK): String → users.userId
- bookId (FK): Int → books.itemId (CASCADE)
- content: String
- pageNumber: Int
- backgroundId: String = ""
- imgUrl: String = ""
- createdAt: Long
- updatedAt: Long
```

### TagEntity (수정됨)
```
Table: tags
- tagId (PK): Long (auto-generated)
- userId (FK): String → users.userId
- name: String
- backgroundColor: String
- textColor: String
- createdAt: Long
```

### BookTagCrossRef (신규)
```
Table: book_tag_cross_ref
- bookId (PK, FK): Int → books.itemId (CASCADE)
- tagId (PK, FK): Long → tags.tagId (CASCADE)
```

## Relationships (완료됨)

```
UserEntity (1) ←→ (N) BookEntity
- 한 사용자는 여러 개의 책을 가질 수 있음
- ✅ BookEntity에 userId 필드 추가됨

BookEntity (1) ←→ (N) MemoEntity
- 한 책은 여러 개의 메모를 가질 수 있음
- ✅ 외래키 관계 설정됨 (bookId → books.itemId)

UserEntity (1) ←→ (N) MemoEntity
- 한 사용자는 여러 개의 메모를 가질 수 있음
- ✅ MemoEntity에 userId 필드 추가됨

UserEntity (1) ←→ (N) TagEntity
- 한 사용자는 여러 개의 태그를 가질 수 있음
- ✅ TagEntity에 userId 필드 추가됨

BookEntity (N) ←→ (N) TagEntity
- 다대다 관계: 한 책은 여러 태그를 가질 수 있고, 한 태그는 여러 책에 적용될 수 있음
- ✅ BookTagCrossRef 중간 테이블 생성됨
```

## Supabase 연동 준비 완료 ✅

모든 엔티티에 userId 필드가 추가되어 사용자별 데이터 분리가 가능합니다:

## 최종 ERD 다이어그램

```
                    ┌─────────────┐
                    │ UserEntity  │
                    │             │
                    │ userId (PK) │
                    │ email       │
                    │ ...         │
                    └─────────────┘
                           │
                    ┌──────┼──────┐
                    │ 1:N  │ 1:N  │ 1:N
                    ▼      ▼      ▼
           ┌─────────────┐ │ ┌─────────────┐
           │ BookEntity  │ │ │ TagEntity   │
           │             │ │ │             │
           │ itemId (PK) │ │ │ tagId (PK)  │
           │ userId (FK) │ │ │ userId (FK) │
           │ ...         │ │ │ ...         │
           └─────────────┘ │ └─────────────┘
                  │        │        │
                  │ 1:N    │        │ N:N
                  ▼        │        ▼
           ┌─────────────┐ │ ┌──────────────────┐
           │ MemoEntity  │ │ │ BookTagCrossRef  │
           │             │ │ │                  │
           │ memoId (PK) │ │ │ bookId (FK)      │
           │ userId (FK) │ │ │ tagId (FK)       │
           │ bookId (FK) │ │ └──────────────────┘
           │ ...         │ │
           └─────────────┘ │
                  ▲        │
                  └────────┘
```