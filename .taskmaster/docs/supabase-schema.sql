-- ====================================
-- SUPABASE DATABASE SCHEMA
-- 오독오독 독서 다이어리 앱
-- ====================================

-- Supabase는 기본적으로 auth.users 테이블을 제공
-- auth.users 테이블 구조:
-- - id: uuid (primary key)
-- - email: varchar
-- - created_at: timestamptz
-- - updated_at: timestamptz
-- - email_confirmed_at: timestamptz
-- - last_sign_in_at: timestamptz
-- - raw_app_meta_data: jsonb
-- - raw_user_meta_data: jsonb

-- 추가 사용자 프로필 정보를 위한 public.profiles 테이블
CREATE TABLE public.profiles (
    id uuid REFERENCES auth.users(id) ON DELETE CASCADE PRIMARY KEY,
    display_name text,
    profile_image_url text,
    provider text DEFAULT 'google',
    created_at timestamptz DEFAULT now(),
    updated_at timestamptz DEFAULT now(),
    last_sync_at timestamptz DEFAULT now(),
    is_active boolean DEFAULT true,
    preferred_language text DEFAULT 'ko',
    timezone text DEFAULT 'Asia/Seoul'
);

-- Books 테이블 (BookEntity 매핑) - UUID 사용
CREATE TABLE public.books (
    item_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid REFERENCES auth.users(id) ON DELETE CASCADE NOT NULL,
    title text NOT NULL,
    author text NOT NULL,
    publisher text NOT NULL,
    isbn text NOT NULL,
    cover_image_url text NOT NULL,
    book_type text DEFAULT '',
    total_page_cnt integer DEFAULT 0,
    current_page_cnt integer DEFAULT 0,
    challenge_page_cnt integer DEFAULT 0,
    start_date text DEFAULT '',
    end_date text DEFAULT '',
    elapsed_time_in_seconds integer DEFAULT 0,
    completed_reading_cnt integer DEFAULT 0,
    description text DEFAULT '',
    rate real DEFAULT 0.0,
    finished_read_cnt integer DEFAULT 0,
    created_at bigint DEFAULT extract(epoch from now()) * 1000,
    updated_at bigint DEFAULT extract(epoch from now()) * 1000
);

-- Memos 테이블 (MemoEntity 매핑) - UUID 사용
CREATE TABLE public.memos (
    memo_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid REFERENCES auth.users(id) ON DELETE CASCADE NOT NULL,
    book_id uuid REFERENCES public.books(item_id) ON DELETE CASCADE NOT NULL,
    content text NOT NULL,
    page_number integer NOT NULL,
    background_id text DEFAULT '',
    img_url text DEFAULT '',
    created_at bigint NOT NULL,
    updated_at bigint DEFAULT extract(epoch from now()) * 1000
);

-- Tags 테이블 (TagEntity 매핑) - UUID 사용
CREATE TABLE public.tags (
    tag_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid REFERENCES auth.users(id) ON DELETE CASCADE NOT NULL,
    name text NOT NULL,
    background_color text NOT NULL,
    text_color text NOT NULL,
    created_at bigint DEFAULT extract(epoch from now()) * 1000
);

-- Book-Tag 다대다 관계 테이블 (BookTagCrossRef 매핑) - UUID 사용
CREATE TABLE public.book_tag_cross_ref (
    book_id uuid REFERENCES public.books(item_id) ON DELETE CASCADE,
    tag_id uuid REFERENCES public.tags(tag_id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, tag_id)
);

-- 인덱스 생성 (성능 최적화)
CREATE INDEX idx_books_user_id ON public.books(user_id);
CREATE INDEX idx_books_isbn ON public.books(isbn);
CREATE INDEX idx_memos_user_id ON public.memos(user_id);
CREATE INDEX idx_memos_book_id ON public.memos(book_id);
CREATE INDEX idx_tags_user_id ON public.tags(user_id);
CREATE INDEX idx_book_tag_cross_ref_book_id ON public.book_tag_cross_ref(book_id);
CREATE INDEX idx_book_tag_cross_ref_tag_id ON public.book_tag_cross_ref(tag_id);

-- Updated_at 자동 업데이트를 위한 함수
CREATE OR REPLACE FUNCTION public.handle_updated_at()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$;

-- Updated_at 트리거 생성
CREATE TRIGGER handle_books_updated_at
    BEFORE UPDATE ON public.books
    FOR EACH ROW
    EXECUTE FUNCTION public.handle_updated_at();

CREATE TRIGGER handle_profiles_updated_at
    BEFORE UPDATE ON public.profiles
    FOR EACH ROW
    EXECUTE FUNCTION public.handle_updated_at();

-- ====================================
-- ROW LEVEL SECURITY (RLS) 정책
-- 사용자별 데이터 격리
-- ====================================

-- RLS 활성화
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.books ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.memos ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.tags ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.book_tag_cross_ref ENABLE ROW LEVEL SECURITY;

-- Profiles 테이블 RLS 정책
CREATE POLICY "Users can view own profile only" ON public.profiles
    FOR SELECT USING (auth.uid() = id);

CREATE POLICY "Users can insert own profile only" ON public.profiles
    FOR INSERT WITH CHECK (auth.uid() = id);

CREATE POLICY "Users can update own profile only" ON public.profiles
    FOR UPDATE USING (auth.uid() = id);

CREATE POLICY "Users can delete own profile only" ON public.profiles
    FOR DELETE USING (auth.uid() = id);

-- Books 테이블 RLS 정책
CREATE POLICY "Users can view own books only" ON public.books
    FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY "Users can insert own books only" ON public.books
    FOR INSERT WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update own books only" ON public.books
    FOR UPDATE USING (auth.uid() = user_id);

CREATE POLICY "Users can delete own books only" ON public.books
    FOR DELETE USING (auth.uid() = user_id);

-- Memos 테이블 RLS 정책
CREATE POLICY "Users can view own memos only" ON public.memos
    FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY "Users can insert own memos only" ON public.memos
    FOR INSERT WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update own memos only" ON public.memos
    FOR UPDATE USING (auth.uid() = user_id);

CREATE POLICY "Users can delete own memos only" ON public.memos
    FOR DELETE USING (auth.uid() = user_id);

-- Tags 테이블 RLS 정책
CREATE POLICY "Users can view own tags only" ON public.tags
    FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY "Users can insert own tags only" ON public.tags
    FOR INSERT WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update own tags only" ON public.tags
    FOR UPDATE USING (auth.uid() = user_id);

CREATE POLICY "Users can delete own tags only" ON public.tags
    FOR DELETE USING (auth.uid() = user_id);

-- Book-Tag Cross Reference 테이블 RLS 정책
CREATE POLICY "Users can view own book-tag relations only" ON public.book_tag_cross_ref
    FOR SELECT USING (
        EXISTS (
            SELECT 1 FROM public.books 
            WHERE books.item_id = book_tag_cross_ref.book_id 
            AND books.user_id = auth.uid()
        )
    );

CREATE POLICY "Users can insert own book-tag relations only" ON public.book_tag_cross_ref
    FOR INSERT WITH CHECK (
        EXISTS (
            SELECT 1 FROM public.books 
            WHERE books.item_id = book_tag_cross_ref.book_id 
            AND books.user_id = auth.uid()
        ) AND
        EXISTS (
            SELECT 1 FROM public.tags 
            WHERE tags.tag_id = book_tag_cross_ref.tag_id 
            AND tags.user_id = auth.uid()
        )
    );

CREATE POLICY "Users can update own book-tag relations only" ON public.book_tag_cross_ref
    FOR UPDATE USING (
        EXISTS (
            SELECT 1 FROM public.books 
            WHERE books.item_id = book_tag_cross_ref.book_id 
            AND books.user_id = auth.uid()
        )
    );

CREATE POLICY "Users can delete own book-tag relations only" ON public.book_tag_cross_ref
    FOR DELETE USING (
        EXISTS (
            SELECT 1 FROM public.books 
            WHERE books.item_id = book_tag_cross_ref.book_id 
            AND books.user_id = auth.uid()
        )
    );