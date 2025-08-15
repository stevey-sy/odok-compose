package com.sy.odokcompose.core.supabase.di

import com.sy.odokcompose.core.supabase.auth.SupabaseAuthService
import com.sy.odokcompose.core.supabase.auth.SupabaseAuthServiceImpl
import com.sy.odokcompose.core.supabase.client.SupabaseClientWrapper
import com.sy.odokcompose.core.supabase.client.SupabaseConfig
import com.sy.odokcompose.core.supabase.sync.SupabaseSyncService
import com.sy.odokcompose.core.supabase.sync.SupabaseSyncServiceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Supabase 관련 의존성 주입 모듈
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SupabaseModule {
    
    @Binds
    @Singleton
    abstract fun bindSupabaseAuthService(
        supabaseAuthServiceImpl: SupabaseAuthServiceImpl
    ): SupabaseAuthService
    
    @Binds
    @Singleton
    abstract fun bindSupabaseSyncService(
        supabaseSyncServiceImpl: SupabaseSyncServiceImpl
    ): SupabaseSyncService
}

/**
 * Supabase 객체 제공 모듈
 */
@Module
@InstallIn(SingletonComponent::class)
object SupabaseProviderModule {
    
    @Provides
    @Singleton
    fun provideSupabaseConfig(): SupabaseConfig {
        // TODO: 실제 Supabase URL과 anon key로 교체
        // 환경변수나 BuildConfig에서 가져오는 것이 좋습니다
        return SupabaseConfig.getDefault()
    }
    
    @Provides
    @Singleton
    fun provideSupabaseClientWrapper(
        config: SupabaseConfig
    ): SupabaseClientWrapper {
        return SupabaseClientWrapper(config)
    }
}