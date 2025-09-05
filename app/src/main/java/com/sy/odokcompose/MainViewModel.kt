package com.sy.odokcompose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sy.odokcompose.core.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 메인 화면의 ViewModel
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _isDataLoading = MutableStateFlow(true)
    
    val uiState: StateFlow<MainUiState> = combine(
        authRepository.getLoginStatusFlow(),
        _isDataLoading
    ) { isLoggedIn, isLoading ->
        when {
            isLoading -> MainUiState.Loading
            !isLoggedIn -> MainUiState.NotLoggedIn
            else -> MainUiState.Success
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState.Loading
    )
    
    init {
        checkLoginStatus()
    }
    
    private fun checkLoginStatus() {
        viewModelScope.launch {
            _isDataLoading.value = true
            // 초기 로딩 로직이 필요하다면 여기에 추가
            _isDataLoading.value = false
        }
    }
    
    fun refreshData() {
        checkLoginStatus()
    }
    
    fun onLoginSuccess() {
        viewModelScope.launch {
            authRepository.setUserLoggedIn(true)
        }
    }
}

/**
 * 메인 화면의 UI 상태를 나타내는 sealed 클래스
 */
sealed class MainUiState {
    // 로딩 중인 상태
    object Loading : MainUiState()
    
    // 로그인되지 않은 상태
    object NotLoggedIn : MainUiState()
    
    // 데이터 로드 성공 상태 (로그인 완료)
    object Success : MainUiState()
} 