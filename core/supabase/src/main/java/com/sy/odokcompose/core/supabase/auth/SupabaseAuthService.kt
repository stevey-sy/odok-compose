package com.sy.odokcompose.core.supabase.auth

import com.sy.odokcompose.core.supabase.auth.model.AuthResult
import com.sy.odokcompose.core.supabase.auth.model.AuthUser
import kotlinx.coroutines.flow.Flow

/**
 * Supabase 인증 서비스 인터페이스
 * 
 * Google OAuth를 통한 사용자 인증 및 세션 관리를 담당합니다.
 */
interface SupabaseAuthService {
    
    /**
     * 현재 인증된 사용자 정보 Flow
     */
    val currentUser: Flow<AuthUser?>
    
    /**
     * 인증 상태 Flow
     */
    val isAuthenticated: Flow<Boolean>
    
    /**
     * Google OAuth로 로그인
     * 
     * @param idToken Google Sign-In에서 받은 ID Token
     * @return 로그인 결과
     */
    suspend fun signInWithGoogle(idToken: String): AuthResult<AuthUser>
    
    /**
     * 로그아웃
     * 
     * @return 로그아웃 결과
     */
    suspend fun signOut(): AuthResult<Unit>
    
    /**
     * 현재 사용자 정보 가져오기 (일회성)
     * 
     * @return 현재 인증된 사용자 또는 null
     */
    suspend fun getCurrentUser(): AuthUser?
    
    /**
     * 액세스 토큰 갱신
     * 
     * @return 갱신된 사용자 정보 또는 에러
     */
    suspend fun refreshToken(): AuthResult<AuthUser>
    
    /**
     * 사용자 프로필 업데이트
     * 
     * @param displayName 새로운 표시 이름
     * @param photoUrl 새로운 프로필 이미지 URL
     * @return 업데이트된 사용자 정보
     */
    suspend fun updateProfile(
        displayName: String? = null,
        photoUrl: String? = null
    ): AuthResult<AuthUser>
    
    /**
     * 계정 삭제
     * 
     * @return 삭제 결과
     */
//    suspend fun deleteAccount(): AuthResult<Unit>
}