package com.sy.feature.timer

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sy.odokcompose.core.domain.GetBookDetailUseCase
import com.sy.odokcompose.core.domain.UpdateBookUseCase
import com.sy.odokcompose.model.BookUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TimerUiState {
    data object BeforeReading : TimerUiState
    data object Reading : TimerUiState
    data object Paused : TimerUiState
    data object Completed : TimerUiState
}

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val getBookDetailUseCase: GetBookDetailUseCase,
    private val updateBookUseCase: UpdateBookUseCase,
    private val savedStateHandle : SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<TimerUiState>(TimerUiState.BeforeReading)
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private val _timerText = MutableStateFlow("00:00:00")
    val timerText: StateFlow<String> = _timerText.asStateFlow()

    private val _guideText = MutableStateFlow<String>("독서를 시작해보세요")
    val guideText: StateFlow<String> = _guideText.asStateFlow()

    private val _book = MutableStateFlow<BookUiModel>(BookUiModel())
    val book: StateFlow<BookUiModel> = _book.asStateFlow()

    private val _backgroundColor = MutableStateFlow(android.graphics.Color.WHITE)
    val backgroundColor: StateFlow<Int> = _backgroundColor.asStateFlow()
    
    private val _textColor = MutableStateFlow(android.graphics.Color.BLACK)
    val textColor: StateFlow<Int> = _textColor.asStateFlow()

    private var timerJob: Job? = null
    private var elapsedSeconds = 0L

    // For Page Input Modal
    private val _isPageInputModalVisible = MutableStateFlow(false)
    val isPageInputModalVisible: StateFlow<Boolean> = _isPageInputModalVisible.asStateFlow()

    private val _lastReadPageInput = MutableStateFlow("")
    val lastReadPageInput: StateFlow<String> = _lastReadPageInput.asStateFlow()

    fun onPlayButtonClick() {
        when (_uiState.value) {
            TimerUiState.BeforeReading, TimerUiState.Paused -> {
                startTimer()
                _uiState.value = TimerUiState.Reading
                _guideText.value = "독서 중..."
                // 배경색/텍스트색 변경은 Screen에서 애니메이션으로 처리하므로 ViewModel에서 직접 변경하지 않음
            }
            TimerUiState.Reading -> {
                pauseTimer()
                _uiState.value = TimerUiState.Paused
                _guideText.value = "일시 정지됨"
            }
            TimerUiState.Completed -> {
                // 완료 상태에서는 Play 버튼 동작 없음
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                elapsedSeconds++
                updateTimerText()
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        // timerJob = null // Setting to null might not be necessary if we always cancel and re-launch
    }

    private fun updateTimerText() {
        val hours = elapsedSeconds / 3600
        val minutes = (elapsedSeconds % 3600) / 60
        val seconds = elapsedSeconds % 60
        _timerText.value = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun onCompleteClick() {
//        if (_uiState.value == TimerUiState.Completed) return // 이미 완료된 경우 중복 실행 방지
        pauseTimer()
        _uiState.value = TimerUiState.Paused
        _guideText.value = "일시 정지됨"
        _isPageInputModalVisible.value = true
        // 페이지 입력 후 저장 시점에 실제 BookUiModel 업데이트 진행
    }

    fun onLastReadPageInputChange(page: String) {
        _lastReadPageInput.value = page
    }

    fun saveLastReadPageAndDismiss() {
        val pageNumber = _lastReadPageInput.value.toIntOrNull()
        viewModelScope.launch {
            try {
                val currentBook = _book.value
                val updatedBook = currentBook.copy(
                    elapsedTimeInSeconds = currentBook.elapsedTimeInSeconds + elapsedSeconds.toInt(),
                    // Assuming BookUiModel has a field like lastReadPage or currentPageCnt
                    // This needs to be adjusted based on your BookUiModel structure
                    currentPageCnt = pageNumber ?: currentBook.currentPageCnt // 예시: 페이지 입력이 있으면 그걸로, 없으면 기존 값
                )
                updateBookUseCase.invoke(updatedBook)
                _book.value = updatedBook // 로컬 상태도 업데이트
                dismissPageInputModal()
                _uiState.value = TimerUiState.Completed
            } catch (e: Exception) {
                Log.e("TimerViewModel", "Error saving last read page", e)
                // TODO: Show error message to user
                dismissPageInputModal() // 에러 발생 시에도 모달은 닫음
            }
        }
    }

    fun dismissPageInputModal() {
        _isPageInputModalVisible.value = false
        _lastReadPageInput.value = "" // 입력 필드 초기화
    }

    private fun loadBookInfo(itemId: Int) {
        viewModelScope.launch {
            getBookDetailUseCase(itemId).collect { book ->
                _book.value = book
            }
        }
    }

    init {
        Log.d("TimerViewModel", "ViewModel created")
        val itemId: Int = checkNotNull(savedStateHandle["itemId"]) { "itemId가 필요합니다." }
        loadBookInfo(itemId)
        // 초기 배경색/텍스트색 설정은 ViewModel에서 제거 (Screen에서 애니메이션 초기값으로 처리)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TimerViewModel", "ViewModel cleared")
        timerJob?.cancel()
    }

}