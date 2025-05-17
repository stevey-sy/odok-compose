package com.sy.odokcompose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 메인 화면의 ViewModel
 */
@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    init {
        loadInitialData()
    }
    
    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading
            
            // 실제 데이터 로딩 로직이 여기에 들어갑니다
            // 예제를 위해 2초 지연을 추가합니다
            delay(2000)
            
            _uiState.value = MainUiState.Success
        }
    }
    
    fun refreshData() {
        loadInitialData()
    }
}

/**
 * 메인 화면의 UI 상태를 나타내는 sealed 클래스
 */
sealed class MainUiState {
    // 로딩 중인 상태
    object Loading : MainUiState()
    
    // 데이터 로드 성공 상태
    object Success : MainUiState()
} 