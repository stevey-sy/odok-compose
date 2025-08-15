package com.sy.odokcompose.core.supabase.auth.model

/**
 * 인증된 사용자 정보
 * 
 * @param id Supabase User ID (UUID)
 * @param email 사용자 이메일
 * @param displayName 사용자 이름 (Google에서 가져옴)
 * @param photoUrl 프로필 이미지 URL
 * @param provider OAuth 제공자 (예: google)
 * @param accessToken JWT Access Token
 * @param refreshToken JWT Refresh Token
 * @param tokenExpiresAt 토큰 만료 시간 (Unix timestamp)
 */
data class AuthUser(
    val id: String,
    val email: String,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val provider: String = "google",
    val accessToken: String,
    val refreshToken: String? = null,
    val tokenExpiresAt: Long = 0L
) {
    /**
     * 토큰이 만료되었는지 확인
     * @param currentTime 현재 시간 (Unix timestamp)
     * @return 만료되었으면 true
     */
    fun isTokenExpired(currentTime: Long = System.currentTimeMillis() / 1000): Boolean {
        return tokenExpiresAt <= currentTime
    }
    
    /**
     * 토큰이 곧 만료될지 확인 (5분 이내)
     * @param currentTime 현재 시간 (Unix timestamp)
     * @param thresholdSeconds 임계값 (초), 기본값 300초 (5분)
     * @return 곧 만료되면 true
     */
    fun isTokenExpiringSoon(
        currentTime: Long = System.currentTimeMillis() / 1000,
        thresholdSeconds: Long = 300L
    ): Boolean {
        return tokenExpiresAt <= (currentTime + thresholdSeconds)
    }
}