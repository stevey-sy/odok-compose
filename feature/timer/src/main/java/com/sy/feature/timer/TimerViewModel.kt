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

    private val _guideText = MutableStateFlow<String>("독서 시간을 측정합니다.")
    val guideText: StateFlow<String> = _guideText.asStateFlow()

    private val _book = MutableStateFlow<BookUiModel>(BookUiModel())
    val book: StateFlow<BookUiModel> = _book.asStateFlow()

    private val _backgroundColor = MutableStateFlow(android.graphics.Color.WHITE)
    val backgroundColor: StateFlow<Int> = _backgroundColor.asStateFlow()
    
    private val _textColor = MutableStateFlow(android.graphics.Color.BLACK)
    val textColor: StateFlow<Int> = _textColor.asStateFlow()

    private var timerJob: Job? = null
    var elapsedSeconds = 0L

    // For Page Input Modal
    private val _isPageInputModalVisible = MutableStateFlow(false)
    val isPageInputModalVisible: StateFlow<Boolean> = _isPageInputModalVisible.asStateFlow()

    private val _isSummaryModalVisible = MutableStateFlow(false)
    val isSummaryModalVisible: StateFlow<Boolean> = _isSummaryModalVisible.asStateFlow()

    private val _isMemoSelectModalVisible = MutableStateFlow(false)
    val isMemoSelectModalVisible: StateFlow<Boolean> = _isMemoSelectModalVisible.asStateFlow()

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

    fun getTotalTime() : String {
        val totalSeconds = elapsedSeconds + book.value.elapsedTimeInSeconds
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun onMemoButtonClick() {
        _isMemoSelectModalVisible.value = true
    }

    fun onDirectInputButtonClick() {
        _isMemoSelectModalVisible.value = false
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

    fun getLastReadPageInt(): Int {
        return _lastReadPageInput.value.toIntOrNull() ?: _book.value.currentPageCnt
    }

    fun getReadPageInt(): Int {
        return _lastReadPageInput.value.toInt() - _book.value.currentPageCnt

    }

    fun saveLastReadPageAndDismiss() {
        viewModelScope.launch {
            // 입력값이 유효한 경우에만 Completed 상태로 변경
            if (_lastReadPageInput.value.isNotBlank()) {
//                _uiState.value = TimerUiState.Completed
                _isSummaryModalVisible.value = true
            }
            dismissPageInputModal()
        }
    }

    fun dismissPageInputModal() {
        _isPageInputModalVisible.value = false
        // 입력값 초기화 제거
    }

    fun dismissMemoSelectModal() {
        _isMemoSelectModalVisible.value = false
    }

    fun dismissSummaryModal() {
        _isSummaryModalVisible.value = false
        _uiState.value = TimerUiState.Completed
    }

    private fun loadBookInfo(itemId: Int) {
        viewModelScope.launch {
            getBookDetailUseCase(itemId).collect { book ->
                _book.value = book
            }
        }
    }

    fun getElapsedTimeSeconds(): Int {
        return elapsedSeconds.toInt()
    }

    fun isBookReadingCompleted(): Boolean {
        val lastReadPage = _lastReadPageInput.value.toIntOrNull() ?: 0
        return lastReadPage > _book.value.currentPageCnt && lastReadPage == _book.value.totalPageCnt
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