package com.sy.odokcompose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sy.odokcompose.core.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        observeFirebaseAuthState()
    }

    private fun observeFirebaseAuthState() {
        firebaseAuth.addAuthStateListener { auth ->
            viewModelScope.launch {
                val currentUser = auth.currentUser
                if (currentUser == null) {
                    // 토큰 만료나 로그아웃 시
                    _authState.value = AuthState.NotAuthenticated
                    clearUserData()
                } else {
                    _authState.value = AuthState.Authenticated(currentUser)
                }
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = authRepository.signInWithGoogle(idToken)
                _authState.value = AuthState.Authenticated(user)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                firebaseAuth.signOut()
                clearUserData()
                _authState.value = AuthState.NotAuthenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e)
            }
        }
    }

    fun handleAuthFailure(exception: Exception) {
        viewModelScope.launch {
            _authState.value = AuthState.Error(exception)
            signOut() // 자동 로그아웃
        }
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.NotAuthenticated
        }
    }

    private suspend fun clearUserData() {
        // 로컬 DB, 캐시 정리 등 필요시 추가
        // 예: userPreferencesRepository.clearUserData()
    }
} 