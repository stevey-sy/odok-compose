package com.sy.feature.timer

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
    val uiState: StateFlow<TimerUiState> = _uiState

    private val _timerText = MutableStateFlow("00:00:00")
    val timerText: StateFlow<String> = _timerText.asStateFlow()

    private val _guideText = MutableStateFlow<String>("독서를 시작해보세요")
    val guideText: StateFlow<String> = _guideText.asStateFlow()

    private val _book = MutableStateFlow<BookUiModel>(BookUiModel())
    val book: StateFlow<BookUiModel> = _book

    private val _backgroundColor = MutableStateFlow(android.graphics.Color.WHITE)
    val backgroundColor: StateFlow<Int> = _backgroundColor.asStateFlow()
    
    private val _textColor = MutableStateFlow(android.graphics.Color.BLACK)
    val textColor: StateFlow<Int> = _textColor.asStateFlow()

    private var timerJob: Job? = null
    private var elapsedSeconds = 0L

    fun onPlayButtonClick() {
        when (_uiState.value) {
            TimerUiState.BeforeReading, TimerUiState.Paused -> {
                startTimer()
                _uiState.value = TimerUiState.Reading
                _guideText.value = "독서 중..."
                _backgroundColor.value = android.graphics.Color.BLACK
                _textColor.value = android.graphics.Color.WHITE
            }
            TimerUiState.Reading -> {
                pauseTimer()
                _uiState.value = TimerUiState.Paused
                _guideText.value = "일시 정지됨"
                _backgroundColor.value = android.graphics.Color.WHITE
                _textColor.value = android.graphics.Color.BLACK
            }
            TimerUiState.Completed -> {
                // 완료 상태에서는 아무 동작도 하지 않음
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
        timerJob = null
    }

    private fun updateTimerText() {
        val hours = elapsedSeconds / 3600
        val minutes = (elapsedSeconds % 3600) / 60
        val seconds = elapsedSeconds % 60
        _timerText.value = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun onCompleteClick() {
        pauseTimer()
        _uiState.value = TimerUiState.Completed
        _guideText.value = "독서 완료!"
        _backgroundColor.value = android.graphics.Color.WHITE
        _textColor.value = android.graphics.Color.BLACK
        
        // TODO: 독서 시간을 저장하는 로직 추가
        viewModelScope.launch {
            try {
                _book.value.let { book ->
                    val updatedBook = book.copy(
                        elapsedTimeInSeconds = book.elapsedTimeInSeconds + elapsedSeconds.toInt()
                    )
                    updateBookUseCase.invoke(updatedBook)
                }
            } catch (e: Exception) {
//                _uiState.update { it.copy(errorMessage = e.message) }
            }


        }
    }

//    fun saveChanges() {
//        viewModelScope.launch {
//            try {
//                val finishedReadCntValue = _finishedReadCnt.value.toIntOrNull() ?: 0
//                val currentPageCntValue = _currentPageCnt.value.toIntOrNull() ?: 0
//
//                currentBook.value?.let { bookToUpdate ->
//                    // 업데이트될 책의 ID를 activeItemId로 설정
//                    // 이렇게 하면 _bookList가 갱신된 후 loadBooksAndSetPage의 collect 블록에서
//                    // 이 책을 기준으로 _currentPage를 올바르게 설정하려고 시도합니다.
//                    activeItemId = bookToUpdate.itemId
//
//                    val updatedBook = bookToUpdate.copy(
//                        finishedReadCnt = finishedReadCntValue,
//                        currentPageCnt = currentPageCntValue
//                    )
//                    updateBookUseCase.invoke(updatedBook)
//                }
//
//                hideEditView() // UI 상태 변경은 작업 완료 후
//            } catch (e: Exception) {
//                _uiState.update { it.copy(errorMessage = e.message) }
//            }
//        }

    private fun loadBookInfo(itemId: Int) {
        viewModelScope.launch {
            getBookDetailUseCase(itemId).collect { book ->
                _book.value = book
            }
        }
    }

    init {
        val itemId: Int = checkNotNull(savedStateHandle["itemId"]) { "itemId가 필요합니다." }
        loadBookInfo(itemId)
        _backgroundColor.value = android.graphics.Color.WHITE
        _textColor.value = android.graphics.Color.BLACK
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}