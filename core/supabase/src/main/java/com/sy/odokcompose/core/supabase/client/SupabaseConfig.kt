package com.sy.odokcompose.core.supabase.client

/**
 * Supabase 설정 데이터 클래스
 * 
 * @param url Supabase 프로젝트 URL
 * @param anonKey Supabase 익명 공개 키 (anon public key)
 */
data class SupabaseConfig(
    val url: String,
    val anonKey: String
) {
    companion object {
        // 개발환경에서는 하드코딩, 실제 배포 시 BuildConfig나 환경변수로 관리
        fun getDefault(): SupabaseConfig {
            return SupabaseConfig(
                url = "YOUR_SUPABASE_URL", // 실제 Supabase URL로 교체 필요
                anonKey = "YOUR_SUPABASE_ANON_KEY" // 실제 Supabase anon key로 교체 필요
            )
        }
    }
}