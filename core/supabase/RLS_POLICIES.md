# Supabase Row Level Security (RLS) 정책

이 문서는 오독오독 앱의 Supabase 데이터베이스에 적용해야 할 RLS 정책들을 정의합니다.

## 개요

RLS (Row Level Security)는 PostgreSQL의 기능으로, 테이블의 각 행에 대한 접근을 사용자별로 제한할 수 있습니다. 
Supabase에서 RLS를 사용하면 JWT 토큰의 사용자 ID를 기반으로 데이터 접근을 자동으로 제한할 수 있습니다.

## 필요한 테이블들

### 1. users 테이블

사용자 기본 정보를 저장하는 테이블입니다.

```sql
-- users 테이블 생성
CREATE TABLE users (
    user_id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT NOT NULL,
    display_name TEXT,
    profile_image_url TEXT,
    provider TEXT DEFAULT 'google',
    preferred_language TEXT DEFAULT 'ko',
    timezone TEXT DEFAULT 'Asia/Seoul',
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    last_sync_at TIMESTAMPTZ DEFAULT NOW()
);

-- 인덱스 생성
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_is_active ON users(is_active);
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_users_last_sync_at ON users(last_sync_at);
```

**RLS 정책:**
```sql
-- RLS 활성화
ALTER TABLE users ENABLE ROW LEVEL SECURITY;

-- 사용자는 자신의 데이터만 조회 가능
CREATE POLICY "Users can view own data" ON users
    FOR SELECT USING (auth.uid() = user_id);

-- 사용자는 자신의 데이터만 삽입 가능
CREATE POLICY "Users can insert own data" ON users
    FOR INSERT WITH CHECK (auth.uid() = user_id);

-- 사용자는 자신의 데이터만 업데이트 가능
CREATE POLICY "Users can update own data" ON users
    FOR UPDATE USING (auth.uid() = user_id);

-- 사용자는 자신의 데이터만 삭제 가능 (계정 삭제 시)
CREATE POLICY "Users can delete own data" ON users
    FOR DELETE USING (auth.uid() = user_id);
```

### 2. books 테이블

사용자의 책 정보를 저장하는 테이블입니다.

```sql
-- books 테이블 생성
CREATE TABLE books (
    item_id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    isbn TEXT NOT NULL,
    title TEXT NOT NULL,
    author TEXT NOT NULL,
    publisher TEXT NOT NULL,
    published_date TEXT DEFAULT '',
    total_page_cnt INTEGER DEFAULT 0,
    current_page_cnt INTEGER DEFAULT 0,
    cover_image_url TEXT DEFAULT '',
    start_date TEXT DEFAULT '',
    end_date TEXT DEFAULT '',
    description TEXT DEFAULT '',
    category TEXT DEFAULT '',
    rate REAL DEFAULT 0.0,
    finished_read_cnt INTEGER DEFAULT 0,
    elapsed_time_in_seconds INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 인덱스 생성
CREATE INDEX idx_books_user_id ON books(user_id);
CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_books_created_at ON books(created_at);
CREATE INDEX idx_books_updated_at ON books(updated_at);
CREATE INDEX idx_books_user_id_updated_at ON books(user_id, updated_at);
```

**RLS 정책:**
```sql
-- RLS 활성화
ALTER TABLE books ENABLE ROW LEVEL SECURITY;

-- 사용자는 자신의 책만 조회 가능
CREATE POLICY "Users can view own books" ON books
    FOR SELECT USING (auth.uid() = user_id);

-- 사용자는 자신의 책만 삽입 가능
CREATE POLICY "Users can insert own books" ON books
    FOR INSERT WITH CHECK (auth.uid() = user_id);

-- 사용자는 자신의 책만 업데이트 가능
CREATE POLICY "Users can update own books" ON books
    FOR UPDATE USING (auth.uid() = user_id);

-- 사용자는 자신의 책만 삭제 가능
CREATE POLICY "Users can delete own books" ON books
    FOR DELETE USING (auth.uid() = user_id);
```

### 3. memos 테이블 (나중에 구현)

사용자의 메모 정보를 저장하는 테이블입니다.

```sql
-- memos 테이블 생성
CREATE TABLE memos (
    memo_id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    book_id UUID NOT NULL REFERENCES books(item_id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    page_number INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 인덱스 생성
CREATE INDEX idx_memos_user_id ON memos(user_id);
CREATE INDEX idx_memos_book_id ON memos(book_id);
CREATE INDEX idx_memos_created_at ON memos(created_at);
CREATE INDEX idx_memos_updated_at ON memos(updated_at);
```

**RLS 정책:**
```sql
-- RLS 활성화
ALTER TABLE memos ENABLE ROW LEVEL SECURITY;

-- 사용자는 자신의 메모만 조회 가능
CREATE POLICY "Users can view own memos" ON memos
    FOR SELECT USING (auth.uid() = user_id);

-- 사용자는 자신의 메모만 삽입 가능
CREATE POLICY "Users can insert own memos" ON memos
    FOR INSERT WITH CHECK (auth.uid() = user_id);

-- 사용자는 자신의 메모만 업데이트 가능
CREATE POLICY "Users can update own memos" ON memos
    FOR UPDATE USING (auth.uid() = user_id);

-- 사용자는 자신의 메모만 삭제 가능
CREATE POLICY "Users can delete own memos" ON memos
    FOR DELETE USING (auth.uid() = user_id);
```

### 4. tags 테이블 (나중에 구현)

태그 정보를 저장하는 테이블입니다.

```sql
-- tags 테이블 생성
CREATE TABLE tags (
    tag_id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    color TEXT DEFAULT '#000000',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(user_id, name)
);

-- 인덱스 생성
CREATE INDEX idx_tags_user_id ON tags(user_id);
CREATE INDEX idx_tags_name ON tags(name);
```

**RLS 정책:**
```sql
-- RLS 활성화
ALTER TABLE tags ENABLE ROW LEVEL SECURITY;

-- 사용자는 자신의 태그만 조회 가능
CREATE POLICY "Users can view own tags" ON tags
    FOR SELECT USING (auth.uid() = user_id);

-- 사용자는 자신의 태그만 삽입 가능
CREATE POLICY "Users can insert own tags" ON tags
    FOR INSERT WITH CHECK (auth.uid() = user_id);

-- 사용자는 자신의 태그만 업데이트 가능
CREATE POLICY "Users can update own tags" ON tags
    FOR UPDATE USING (auth.uid() = user_id);

-- 사용자는 자신의 태그만 삭제 가능
CREATE POLICY "Users can delete own tags" ON tags
    FOR DELETE USING (auth.uid() = user_id);
```

## 트리거 함수

updated_at 필드를 자동으로 업데이트하는 트리거 함수를 생성합니다.

```sql
-- updated_at 자동 업데이트 함수
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 각 테이블에 트리거 적용
CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON users 
    FOR EACH ROW 
    EXECUTE PROCEDURE update_updated_at_column();

CREATE TRIGGER update_books_updated_at 
    BEFORE UPDATE ON books 
    FOR EACH ROW 
    EXECUTE PROCEDURE update_updated_at_column();

CREATE TRIGGER update_memos_updated_at 
    BEFORE UPDATE ON memos 
    FOR EACH ROW 
    EXECUTE PROCEDURE update_updated_at_column();

CREATE TRIGGER update_tags_updated_at 
    BEFORE UPDATE ON tags 
    FOR EACH ROW 
    EXECUTE PROCEDURE update_updated_at_column();
```

## Google OAuth 설정

Supabase Dashboard에서 Google OAuth를 설정해야 합니다.

1. **Google Cloud Console에서 OAuth 2.0 클라이언트 ID 생성**
   - Google Cloud Console → API 및 서비스 → 사용자 인증 정보
   - OAuth 2.0 클라이언트 ID 생성
   - Android 앱용으로 설정

2. **Supabase Dashboard에서 Google 설정**
   - Authentication → Providers → Google
   - Enable Google provider
   - Client ID와 Client Secret 입력
   - Authorized redirect URLs 설정

## 보안 고려사항

### 1. JWT 토큰 검증
- 모든 RLS 정책은 `auth.uid()`를 사용하여 현재 인증된 사용자의 ID를 확인
- JWT 토큰이 없거나 만료된 경우 모든 데이터 접근 차단

### 2. 사용자 데이터 격리
- 각 사용자는 오직 자신의 데이터에만 접근 가능
- `user_id` 필드를 통한 완전한 데이터 격리

### 3. 외래 키 제약
- 모든 테이블이 `users` 테이블을 참조하여 데이터 일관성 보장
- CASCADE DELETE로 사용자 삭제 시 관련 데이터 모두 삭제

### 4. 인덱스 최적화
- `user_id`와 `updated_at` 복합 인덱스로 동기화 쿼리 최적화
- 자주 사용되는 필터 조건에 대한 인덱스 생성

## 동기화 시나리오

### 1. 초기 동기화
```sql
-- 사용자의 모든 책 조회 (RLS에 의해 자동 필터링)
SELECT * FROM books ORDER BY created_at DESC;
```

### 2. 증분 동기화
```sql
-- 특정 시간 이후 변경된 책 조회
SELECT * FROM books 
WHERE updated_at > '2023-01-01T00:00:00Z'
ORDER BY updated_at ASC;
```

### 3. 충돌 해결
- Last-Write-Wins 전략 사용
- `updated_at` 필드를 비교하여 최신 데이터 선택

## 테스트 방법

### 1. RLS 정책 테스트
```sql
-- 다른 사용자의 데이터 접근 시도 (실패해야 함)
SELECT * FROM books WHERE user_id = 'other-user-id';
```

### 2. 성능 테스트
```sql
-- 인덱스 사용 확인
EXPLAIN (ANALYZE, BUFFERS) 
SELECT * FROM books WHERE user_id = auth.uid();
```

이 RLS 정책들을 Supabase Dashboard의 SQL 에디터에서 실행하면 완전한 사용자 데이터 격리가 구현됩니다.