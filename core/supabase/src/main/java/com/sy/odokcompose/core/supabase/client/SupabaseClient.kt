package com.sy.odokcompose.core.supabase.client

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Supabase 클라이언트 래퍼
 * 
 * Supabase 클라이언트를 초기화하고 주요 모듈들에 대한 접근을 제공합니다.
 */
@Singleton
class SupabaseClientWrapper @Inject constructor(
    private val config: SupabaseConfig
) {
    private val _supabaseClient: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = config.url,
            supabaseKey = config.anonKey
        ) {
            install(Auth) {
                // 기본 설정
            }
            install(Postgrest) {
                // 기본 설정
            }
            install(Realtime) {
                // 실시간 구독용 (나중에 활용 가능)
            }
        }
    }
    
    /**
     * Supabase 클라이언트 인스턴스
     */
    val client: SupabaseClient
        get() = _supabaseClient
    
    /**
     * Auth 모듈 - 사용자 인증 관리
     */
    val auth: Auth
        get() = _supabaseClient.auth
    
    /**
     * Postgrest 모듈 - 데이터베이스 CRUD 작업
     */
    val postgrest: Postgrest
        get() = _supabaseClient.postgrest
    
    /**
     * Realtime 모듈 - 실시간 데이터 구독
     */
    val realtime: Realtime
        get() = _supabaseClient.realtime
}