package com.sy.feature.memo

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.sy.odokcompose.core.designsystem.icon.OdokIcons
import com.sy.odokcompose.core.domain.GetMemoUseCase
import com.sy.odokcompose.core.domain.SaveMemoUseCase
import com.sy.odokcompose.core.domain.UpdateMemoResult
import com.sy.odokcompose.core.domain.UpdateMemoUseCase
import com.sy.odokcompose.model.MemoUiModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed class AddMemoUiState {
    object CreateMode : AddMemoUiState()
    object EditMode : AddMemoUiState()
}

sealed class AddMemoEvent {
    object HandleCloseButton : AddMemoEvent()
    object HandleSaveButton : AddMemoEvent()
    object MemoSaved : AddMemoEvent()
    data class ShowMessage(val message: String) : AddMemoEvent()
}

@HiltViewModel
class AddMemoViewModel @Inject constructor(
    private val getMemoUseCase: GetMemoUseCase,
    private val saveMemoUseCase: SaveMemoUseCase,
    private val updateMemoUseCase: UpdateMemoUseCase,
    private val savedStateHandle : SavedStateHandle
) : ViewModel() {

    private val itemId: Int = checkNotNull(savedStateHandle["itemId"]) { "itemId가 필요합니다." }
    private val memoId: Int = savedStateHandle["memoId"] ?: -1

    private val _eventChannel = Channel<AddMemoEvent>(Channel.UNLIMITED)
    val eventFlow = _eventChannel.receiveAsFlow()

    private val _uiState = MutableStateFlow<AddMemoUiState>(
        if (memoId == -1) AddMemoUiState.CreateMode else AddMemoUiState.EditMode
    )
    val uiState: StateFlow<AddMemoUiState> = _uiState.asStateFlow()

    private val _pageText = MutableStateFlow(TextFieldValue(""))
    val pageText: StateFlow<TextFieldValue> = _pageText.asStateFlow()

    private val _memoText = MutableStateFlow(TextFieldValue(""))
    val memoText: StateFlow<TextFieldValue> = _memoText.asStateFlow()

    private val _selectedPaperType = MutableStateFlow("white_paper")
    val selectedPaperType: StateFlow<String> = _selectedPaperType.asStateFlow()

    private var originalMemo: MemoUiModel? = null

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    var displayDate: String = getTodayDate()
        private set

    init {
        if (memoId != -1) {
            try {
                viewModelScope.launch {
                    getMemoUseCase.invoke(memoId)?.let { memo ->
                        originalMemo = memo
                        _pageText.value = TextFieldValue(memo.pageNumber.toString())
                        _memoText.value = TextFieldValue(memo.content)
                        _selectedPaperType.value = memo.backgroundId
                        displayDate = memo.getCreateDateText()
                    } ?: run {
                        handleEvent(AddMemoEvent.ShowMessage("메모를 찾을 수 없습니다."))
                    }
                }
            } catch (e: Exception) {
                handleEvent(AddMemoEvent.ShowMessage(e.message ?: "알 수 없는 에러가 발생했습니다."))
            }
        }
    }

    fun handleEvent(event: AddMemoEvent) {
        when(event) {
            AddMemoEvent.HandleCloseButton ->  sendEvent(event)
            AddMemoEvent.MemoSaved -> sendEvent(event)
            is AddMemoEvent.ShowMessage -> sendEvent(event)
            AddMemoEvent.HandleSaveButton -> {
                when (_uiState.value) {
                    is AddMemoUiState.CreateMode -> addMemo()
                    is AddMemoUiState.EditMode -> editMemo()
                }
            }
        }
    }

    private val paperTypes = listOf(
        "white_paper",
        "old_paper",
        "dot_paper",
        "blue_sky",
        "yellow_paper"
    )

    fun getTodayDate(): String {
        return SimpleDateFormat("yyyy. MM. dd", Locale.getDefault()).format(Date())
    }

    fun updatePageText(newValue: TextFieldValue) {
        _pageText.value = newValue
    }

    fun updateMemoText(newValue: TextFieldValue) {
        _memoText.value = newValue
    }

    fun updatePaperType(index: Int) {
        if (index in paperTypes.indices) {
            _selectedPaperType.value = paperTypes[index]
        }
    }

    fun editMemo() {
        if (_isSaving.value) return

        viewModelScope.launch {
            try {
                _isSaving.value = true
                val page = _pageText.value.text.ifEmpty { "0" }.toInt()
                val memoText = _memoText.value.text
                val paperType = _selectedPaperType.value

                if (memoText.isNotBlank() && originalMemo != null) {
                    val updatedMemo = originalMemo!!.copy(
                        pageNumber = page,
                        content = memoText,
                        backgroundId = paperType
                    )
                    
                    when (updateMemoUseCase(updatedMemo)) {
                        is UpdateMemoResult.Success -> handleEvent(AddMemoEvent.MemoSaved)
                        is UpdateMemoResult.Error -> handleEvent(AddMemoEvent.ShowMessage((updateMemoUseCase(updatedMemo) as UpdateMemoResult.Error).message))
                    }
                }
            } catch (e: Exception) {
                handleEvent(AddMemoEvent.ShowMessage(e.message ?: "메모 수정에 실패했습니다."))
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun addMemo() {
        if (_isSaving.value) return
        viewModelScope.launch {
            try {
                _isSaving.value = true
                val page = _pageText.value.text.ifEmpty { "0" }.toInt()
                val memo = _memoText.value.text
                val paperType = _selectedPaperType.value

                if (memo.isNotBlank()) {
                    saveMemoUseCase(itemId, page, memo, paperType)
                    handleEvent(AddMemoEvent.MemoSaved)
                }
            } catch (e: Exception) {
                handleEvent(AddMemoEvent.ShowMessage(e.message ?: "메모 저장에 실패했습니다."))
            } finally {
                _isSaving.value = false
            }
        }
    }

    private fun sendEvent(event: AddMemoEvent) {
        viewModelScope.launch { _eventChannel.send(event) }
    }
}