package com.sy.odokcompose.core.supabase.auth

import android.util.Log
import com.sy.odokcompose.core.database.dao.AuthTokenDao
import com.sy.odokcompose.core.database.dao.UserDao
import com.sy.odokcompose.core.database.entity.AuthTokenEntity
import com.sy.odokcompose.core.database.entity.UserEntity
import com.sy.odokcompose.core.supabase.auth.model.AuthResult
import com.sy.odokcompose.core.supabase.auth.model.AuthUser
import com.sy.odokcompose.core.supabase.client.SupabaseClientWrapper
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Supabase 인증 서비스 구현
 */
@Singleton
class SupabaseAuthServiceImpl @Inject constructor(
    private val supabaseClient: SupabaseClientWrapper,
    private val userDao: UserDao,
    private val authTokenDao: AuthTokenDao
) : SupabaseAuthService {
    
    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    override val currentUser: Flow<AuthUser?> = _currentUser.asStateFlow()
    
    override val isAuthenticated: Flow<Boolean> = currentUser.map { it != null }
    
    companion object {
        private const val TAG = "SupabaseAuthService"
    }
    
    init {
        // 앱 시작 시 저장된 세션 복원 시도
        restoreSession()
    }
    
    override suspend fun signInWithGoogle(idToken: String): AuthResult<AuthUser> {
        return try {
            Log.d(TAG, "Google 로그인 시작")
            
            // Supabase Google OAuth 로그인
            val session = supabaseClient.auth.signInWith(IDToken) {
                this.idToken = idToken
                provider = Google
            }
            
            Log.d(TAG, "Supabase 로그인 성공: ${session.user?.id}")
            
            // AuthUser 객체 생성
            val authUser = session.user?.let { user ->
                AuthUser(
                    id = user.id,
                    email = user.email ?: "",
                    displayName = user.userMetadata?.get("full_name") as? String,
                    photoUrl = user.userMetadata?.get("avatar_url") as? String,
                    provider = "google",
                    accessToken = session.accessToken,
                    refreshToken = session.refreshToken,
                    tokenExpiresAt = session.expiresAt?.let { it / 1000 } ?: 0L
                )
            }
            
            if (authUser == null) {
                return AuthResult.Error(
                    exception = IllegalStateException("사용자 정보를 가져올 수 없습니다"),
                    message = "로그인에 실패했습니다"
                )
            }
            
            // 로컬 데이터베이스에 사용자 정보 저장/업데이트
            saveUserToDatabase(authUser)
            
            // 현재 사용자 상태 업데이트
            _currentUser.value = authUser
            
            Log.d(TAG, "로그인 완료: ${authUser.email}")
            AuthResult.Success(authUser)
            
        } catch (e: Exception) {
            Log.e(TAG, "Google 로그인 실패", e)
            AuthResult.Error(
                exception = e,
                message = "로그인에 실패했습니다: ${e.message}"
            )
        }
    }
    
    override suspend fun signOut(): AuthResult<Unit> {
        return try {
            Log.d(TAG, "로그아웃 시작")
            
            // 현재 사용자 ID 가져오기
            val currentUserId = _currentUser.value?.id
            
            // Supabase 로그아웃
            supabaseClient.auth.signOut()
            
            // 로컬 토큰 정리
            currentUserId?.let { userId ->
                authTokenDao.deactivateAllUserTokens(userId)
            }
            
            // 현재 사용자 상태 초기화
            _currentUser.value = null
            
            Log.d(TAG, "로그아웃 완료")
            AuthResult.Success(Unit)
            
        } catch (e: Exception) {
            Log.e(TAG, "로그아웃 실패", e)
            AuthResult.Error(
                exception = e,
                message = "로그아웃에 실패했습니다: ${e.message}"
            )
        }
    }
    
    override suspend fun getCurrentUser(): AuthUser? {
        return _currentUser.value
    }
    
    override suspend fun refreshToken(): AuthResult<AuthUser> {
        return try {
            Log.d(TAG, "토큰 갱신 시작")
            
            val session = supabaseClient.auth.refreshCurrentSession()
            
            val authUser = session.user?.let { user ->
                AuthUser(
                    id = user.id,
                    email = user.email ?: "",
                    displayName = user.userMetadata?.get("full_name") as? String,
                    photoUrl = user.userMetadata?.get("avatar_url") as? String,
                    provider = "google",
                    accessToken = session.accessToken,
                    refreshToken = session.refreshToken,
                    tokenExpiresAt = session.expiresAt?.let { it / 1000 } ?: 0L
                )
            }
            
            if (authUser == null) {
                return AuthResult.Error(
                    exception = IllegalStateException("사용자 정보를 가져올 수 없습니다"),
                    message = "토큰 갱신에 실패했습니다"
                )
            }
            
            // 로컬 토큰 업데이트
            updateTokenInDatabase(authUser)
            
            // 현재 사용자 상태 업데이트
            _currentUser.value = authUser
            
            Log.d(TAG, "토큰 갱신 완료")
            AuthResult.Success(authUser)
            
        } catch (e: Exception) {
            Log.e(TAG, "토큰 갱신 실패", e)
            AuthResult.Error(
                exception = e,
                message = "토큰 갱신에 실패했습니다: ${e.message}"
            )
        }
    }
    
    override suspend fun updateProfile(
        displayName: String?,
        photoUrl: String?
    ): AuthResult<AuthUser> {
        return try {
            Log.d(TAG, "프로필 업데이트 시작")
            
            val updateData = mutableMapOf<String, Any?>()
            displayName?.let { updateData["full_name"] = it }
            photoUrl?.let { updateData["avatar_url"] = it }
            
            val updatedUser = supabaseClient.auth.updateUser {
                data = updateData
            }
            
            val currentUser = _currentUser.value
            if (currentUser == null) {
                return AuthResult.Error(
                    exception = IllegalStateException("로그인이 필요합니다"),
                    message = "로그인 후 다시 시도해주세요"
                )
            }
            
            val authUser = currentUser.copy(
                displayName = displayName ?: currentUser.displayName,
                photoUrl = photoUrl ?: currentUser.photoUrl
            )
            
            // 로컬 사용자 정보 업데이트
            updateUserInDatabase(authUser)
            
            // 현재 사용자 상태 업데이트
            _currentUser.value = authUser
            
            Log.d(TAG, "프로필 업데이트 완료")
            AuthResult.Success(authUser)
            
        } catch (e: Exception) {
            Log.e(TAG, "프로필 업데이트 실패", e)
            AuthResult.Error(
                exception = e,
                message = "프로필 업데이트에 실패했습니다: ${e.message}"
            )
        }
    }
    
    override suspend fun deleteAccount(): AuthResult<Unit> {
        return try {
            Log.d(TAG, "계정 삭제 시작")
            
            val currentUserId = _currentUser.value?.id
            if (currentUserId == null) {
                return AuthResult.Error(
                    exception = IllegalStateException("로그인이 필요합니다"),
                    message = "로그인 후 다시 시도해주세요"
                )
            }
            
            // Supabase에서는 직접적인 사용자 삭제 API가 없으므로
            // 사용자를 비활성화하고 로컬 데이터 정리
            
            // 로컬 사용자 데이터 비활성화
            val userEntity = userDao.getUserByIdSync(currentUserId)
            userEntity?.let {
                userDao.updateUser(it.copy(isActive = false))
            }
            
            // 모든 토큰 비활성화
            authTokenDao.deactivateAllUserTokens(currentUserId)
            
            // 로그아웃
            supabaseClient.auth.signOut()
            _currentUser.value = null
            
            Log.d(TAG, "계정 삭제 완료")
            AuthResult.Success(Unit)
            
        } catch (e: Exception) {
            Log.e(TAG, "계정 삭제 실패", e)
            AuthResult.Error(
                exception = e,
                message = "계정 삭제에 실패했습니다: ${e.message}"
            )
        }
    }
    
    /**
     * 저장된 세션 복원
     */
    private fun restoreSession() {
        try {
            // Supabase 세션이 있는지 확인
            val session = supabaseClient.auth.currentSessionOrNull()
            if (session != null && session.user != null) {
                val authUser = AuthUser(
                    id = session.user!!.id,
                    email = session.user!!.email ?: "",
                    displayName = session.user!!.userMetadata?.get("full_name") as? String,
                    photoUrl = session.user!!.userMetadata?.get("avatar_url") as? String,
                    provider = "google",
                    accessToken = session.accessToken,
                    refreshToken = session.refreshToken,
                    tokenExpiresAt = session.expiresAt?.let { it / 1000 } ?: 0L
                )
                
                _currentUser.value = authUser
                Log.d(TAG, "세션 복원 완료: ${authUser.email}")
            }
        } catch (e: Exception) {
            Log.w(TAG, "세션 복원 실패", e)
        }
    }
    
    /**
     * 사용자 정보를 로컬 데이터베이스에 저장
     */
    private suspend fun saveUserToDatabase(authUser: AuthUser) {
        try {
            // UserEntity 저장/업데이트
            val userEntity = UserEntity(
                userId = authUser.id,
                email = authUser.email,
                displayName = authUser.displayName,
                profileImageUrl = authUser.photoUrl,
                provider = authUser.provider,
                updatedAt = System.currentTimeMillis(),
                lastSyncAt = System.currentTimeMillis()
            )
            
            userDao.upsertUser(userEntity)
            
            // AuthTokenEntity 저장
            val accessTokenEntity = AuthTokenEntity(
                userId = authUser.id,
                token = authUser.accessToken,
                tokenType = AuthTokenEntity.TOKEN_TYPE_ACCESS,
                expiresAt = authUser.tokenExpiresAt * 1000 // 밀리초로 변환
            )
            
            authTokenDao.replaceAccessToken(authUser.id, accessTokenEntity)
            
            // Refresh Token이 있다면 저장
            authUser.refreshToken?.let { refreshToken ->
                val refreshTokenEntity = AuthTokenEntity(
                    userId = authUser.id,
                    token = refreshToken,
                    tokenType = AuthTokenEntity.TOKEN_TYPE_REFRESH,
                    expiresAt = (authUser.tokenExpiresAt + 30 * 24 * 60 * 60) * 1000 // 30일 후 만료
                )
                
                authTokenDao.replaceRefreshToken(authUser.id, refreshTokenEntity)
            }
            
            Log.d(TAG, "사용자 정보 로컬 저장 완료: ${authUser.email}")
            
        } catch (e: Exception) {
            Log.e(TAG, "사용자 정보 로컬 저장 실패", e)
        }
    }
    
    /**
     * 토큰 정보를 로컬 데이터베이스에 업데이트
     */
    private suspend fun updateTokenInDatabase(authUser: AuthUser) {
        try {
            val accessTokenEntity = AuthTokenEntity(
                userId = authUser.id,
                token = authUser.accessToken,
                tokenType = AuthTokenEntity.TOKEN_TYPE_ACCESS,
                expiresAt = authUser.tokenExpiresAt * 1000
            )
            
            authTokenDao.replaceAccessToken(authUser.id, accessTokenEntity)
            
            authUser.refreshToken?.let { refreshToken ->
                val refreshTokenEntity = AuthTokenEntity(
                    userId = authUser.id,
                    token = refreshToken,
                    tokenType = AuthTokenEntity.TOKEN_TYPE_REFRESH,
                    expiresAt = (authUser.tokenExpiresAt + 30 * 24 * 60 * 60) * 1000
                )
                
                authTokenDao.replaceRefreshToken(authUser.id, refreshTokenEntity)
            }
            
            Log.d(TAG, "토큰 정보 업데이트 완료")
            
        } catch (e: Exception) {
            Log.e(TAG, "토큰 정보 업데이트 실패", e)
        }
    }
    
    /**
     * 사용자 정보를 로컬 데이터베이스에 업데이트
     */
    private suspend fun updateUserInDatabase(authUser: AuthUser) {
        try {
            val existingUser = userDao.getUserByIdSync(authUser.id)
            existingUser?.let { user ->
                val updatedUser = user.copy(
                    displayName = authUser.displayName,
                    profileImageUrl = authUser.photoUrl,
                    updatedAt = System.currentTimeMillis()
                )
                
                userDao.updateUser(updatedUser)
                Log.d(TAG, "사용자 정보 업데이트 완료")
            }
        } catch (e: Exception) {
            Log.e(TAG, "사용자 정보 업데이트 실패", e)
        }
    }
}