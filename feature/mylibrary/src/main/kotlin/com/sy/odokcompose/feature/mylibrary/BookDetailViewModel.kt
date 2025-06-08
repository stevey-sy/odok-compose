package com.sy.odokcompose.feature.mylibrary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sy.odokcompose.core.domain.DeleteMemoResult
import com.sy.odokcompose.core.domain.DeleteMemoUseCase
import com.sy.odokcompose.core.domain.GetMemosUseCase
import com.sy.odokcompose.core.domain.GetMyBooksUseCase
import com.sy.odokcompose.core.domain.UpdateBookUseCase
import com.sy.odokcompose.model.BookUiModel
import com.sy.odokcompose.model.MemoUiModel
import com.sy.odokcompose.model.type.ShelfFilterType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookDetailUiState(
    val isLoading: Boolean = false,
    val isEditViewShowing: Boolean = false,
    val isMemoListShowing: Boolean = false,
    val isMemoViewShowing: Boolean = false,
)

sealed class BookDetailEvent {
    data class HandleReadButton(val itemId: Int) : BookDetailEvent()
    data class HandleMemoDeleteButton(val memoId: Int) : BookDetailEvent()
    data class HandleMemoEditButton(val bookId: Int, val memoId: Int): BookDetailEvent()
    data class ShowError(val message: String) : BookDetailEvent()
    data class HandleCommentButton(val itemId: Int): BookDetailEvent()
    object ShowDeleteSuccess : BookDetailEvent()
}

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val getMyBooksUseCase: GetMyBooksUseCase,
    private val getMemosUseCase: GetMemosUseCase,
    private val deleteMemoUseCase: DeleteMemoUseCase,
    private val updateBookUseCase: UpdateBookUseCase,
    private val savedStateHandle : SavedStateHandle,
) : ViewModel() {
    private val _eventChannel = Channel<BookDetailEvent>(Channel.UNLIMITED)
    val eventFlow = _eventChannel.receiveAsFlow()

    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState : StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    private val _bookList = MutableStateFlow<List<BookUiModel>>(emptyList())
    val bookList: StateFlow<List<BookUiModel>> = _bookList.asStateFlow()

    private val _memoList = MutableStateFlow<List<MemoUiModel>>(emptyList())
    val memoList: StateFlow<List<MemoUiModel>> = _memoList.asStateFlow()

    // 현재 페이지(선택된 책의 index)
    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    // 현재 선택된 책
    val currentBook: StateFlow<BookUiModel?> = combine(_bookList, _currentPage) { list, idx ->
        list.getOrNull(idx)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    // 수정 관련 상태
    private val _finishedReadCnt = MutableStateFlow("0")
    val finishedReadCnt: StateFlow<String> = _finishedReadCnt.asStateFlow()

    private val _currentPageCnt = MutableStateFlow("0")
    val currentPageCnt: StateFlow<String> = _currentPageCnt.asStateFlow()

    // 현재 보고 있는(또는 마지막으로 본) 책의 itemId를 추적하기 위한 변수
    private val _activeItemId = MutableStateFlow<Int?>(null)
    val activeItemId: StateFlow<Int?> = _activeItemId.asStateFlow()

    init {
        // navArgs로 전달된 itemId 가져오기
        val itemIdFromNav: Int? = savedStateHandle["itemId"]
        _activeItemId.value = itemIdFromNav // 초기 activeItemId 설정

        val filterTypeInt: Int? = savedStateHandle["filterType"]
        val filterType = filterTypeInt?.let { ShelfFilterType.fromCode(it) } ?: ShelfFilterType.NONE
        val searchQuery: String? = savedStateHandle["searchQuery"] ?: ""
        loadBooksAndSetPage(filterType, searchQuery)
        collectMemos() // 메모 수집 시작
    }

    // screen 에서 일어나는 모든 이벤트를 관리.
    fun handleEvent(event: BookDetailEvent) {
        when (event) {
            is BookDetailEvent.HandleReadButton,
            is BookDetailEvent.HandleMemoDeleteButton,
            is BookDetailEvent.ShowDeleteSuccess,
            is BookDetailEvent.HandleCommentButton,
            is BookDetailEvent.ShowError -> {
                sendEvent(event)
            }
            is BookDetailEvent.HandleMemoEditButton -> {
//                hideMemoListView()
                sendEvent(event)
            }
            else -> { /* 추가 이벤트 처리 */ }
        }
    }

    private fun sendEvent(event: BookDetailEvent) {
        viewModelScope.launch { _eventChannel.send(event) }
    }

    private fun loadBooksAndSetPage(
        filterType: ShelfFilterType = ShelfFilterType.NONE,
        searchQuery: String?
    ) {
        viewModelScope.launch {
            // 책 목록 Flow 수집
            getMyBooksUseCase(filterType).collect { books ->
                val filteredBooks = if (!searchQuery.isNullOrEmpty()) {
                    books.filter { book ->
                        book.title.lowercase().contains(searchQuery.lowercase()) ||
                        book.author.lowercase().contains(searchQuery.lowercase())
                    }
                } else {
                    books
                }
                _bookList.value = filteredBooks

                var determinedPageIndex = -1

                // 1. activeItemId가 있고, 해당 아이템이 새 리스트에 존재하면 그 인덱스 사용
                if (_activeItemId.value != null) {
                    val indexByActiveItemId = filteredBooks.indexOfFirst { it.itemId == _activeItemId.value }
                    if (indexByActiveItemId != -1) {
                        determinedPageIndex = indexByActiveItemId
                    }
                }

                // 2. 위에서 페이지를 결정하지 못했고, NavArgs로 전달된 초기 itemId가 있다면 그것으로 시도
                if (determinedPageIndex == -1) {
                    val initialItemIdFromNav: Int? = savedStateHandle["itemId"]
                    if (initialItemIdFromNav != null) {
                        val indexByInitialItemId = filteredBooks.indexOfFirst { it.itemId == initialItemIdFromNav }
                        if (indexByInitialItemId != -1) {
                            determinedPageIndex = indexByInitialItemId
                        }
                    }
                }

                // 3. 그래도 페이지를 결정하지 못했다면, 리스트가 비어있지 않으면 0번째 인덱스 사용
                if (determinedPageIndex == -1 && filteredBooks.isNotEmpty()) {
                    determinedPageIndex = 0
                }

                // 4. 최종적으로 결정된 페이지 인덱스를 _currentPage에 반영
                _currentPage.value = determinedPageIndex

                // 5. 현재 페이지가 유효한 경우, activeItemId를 현재 페이지의 itemId와 동기화
                if (determinedPageIndex != -1 && filteredBooks.indices.contains(determinedPageIndex)) {
                    val currentItemInPager = filteredBooks.getOrNull(determinedPageIndex)
                    _activeItemId.value = currentItemInPager?.itemId
                } else if (filteredBooks.isEmpty()) {
                    _activeItemId.value = null // 리스트가 비면 activeItemId도 초기화
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun collectMemos() {
//        viewModelScope.launch {
//            _activeItemId.collect { itemId ->
//                itemId?.let { id ->
//                    getMemosUseCase(id).collect { memos ->
//                        _memoList.value = memos
//                    }
//                } ?: run {
//                    _memoList.value = emptyList()
//                }
//            }
//        }
        viewModelScope.launch {
            _activeItemId
                .filterNotNull()
                .flatMapLatest { itemId ->
                    getMemosUseCase(itemId)
                }
                .collect { memos ->
                    _memoList.value = memos
                }
        }
    }

    fun updateBookWithTimerData(lastReadPage: Int, elapsedTimeSeconds: Int, isBookReadingCompleted: Boolean) {
        viewModelScope.launch {
            try {
                currentBook.value?.let { bookToUpdate ->
                    val updatedBook = bookToUpdate.copy(
                        currentPageCnt = lastReadPage,
                        elapsedTimeInSeconds = bookToUpdate.elapsedTimeInSeconds + elapsedTimeSeconds,
                        finishedReadCnt = if (isBookReadingCompleted) {
                            bookToUpdate.finishedReadCnt + 1
                        } else {
                            bookToUpdate.finishedReadCnt
                        }
                    )
                    updateBookUseCase.invoke(updatedBook)
                }
            } catch (e: Exception) {
                handleEvent(BookDetailEvent.ShowError(e.message ?: "알 수 없는 에러가 발생했습니다."))
            }
        }
    }

    // ViewPager에서 페이지 변경 시 호출
    fun onPageChanged(newIndex: Int) {
        _currentPage.value = newIndex
        // 페이지가 변경되면 activeItemId도 현재 페이지의 책 ID로 업데이트
        _activeItemId.value = _bookList.value.getOrNull(newIndex)?.itemId
    }

    fun showEditView() {
        _uiState.update { it.copy(isEditViewShowing = true) }
        currentBook.value?.let { book ->
            _finishedReadCnt.value = book.finishedReadCnt.toString()
            _currentPageCnt.value = book.currentPageCnt.toString()
        }
    }

    fun hideEditView() {
        _uiState.update { it.copy(isEditViewShowing = false) }
    }

    // ViewModel
    fun showMemoListView() {
        if(_memoList.value.isEmpty()) return
        _uiState.update { it.copy(isMemoListShowing = true) }
    }

    fun hideMemoListView() {
        _uiState.update { it.copy(isMemoListShowing = false) }
    }

    fun updateFinishedReadCnt(value: String) {
        _finishedReadCnt.value = value
    }

    fun updateCurrentPageCnt(value: String) {
        _currentPageCnt.value = value
    }

    fun deleteMemoById(memoId: Int) {
        viewModelScope.launch {
            try {
                when (val result = deleteMemoUseCase(memoId)) {
                    is DeleteMemoResult.Success -> {
                        handleEvent(BookDetailEvent.ShowDeleteSuccess)
                    }
                    is DeleteMemoResult.Error -> {
                        handleEvent(BookDetailEvent.ShowError(result.meesage))
                    }
                }
            } catch (e: Exception) {
                handleEvent(BookDetailEvent.ShowError(e.message ?: "알 수 없는 에러가 발생했습니다."))
            }
        }
    }

    fun saveChanges() {
        viewModelScope.launch {
            try {
                val finishedReadCntValue = _finishedReadCnt.value.toIntOrNull() ?: 0
                val currentPageCntValue = _currentPageCnt.value.toIntOrNull() ?: 0
                
                currentBook.value?.let { bookToUpdate ->
                    // 업데이트될 책의 ID를 activeItemId로 설정
                    _activeItemId.value = bookToUpdate.itemId

                    val updatedBook = bookToUpdate.copy(
                        finishedReadCnt = finishedReadCntValue,
                        currentPageCnt = currentPageCntValue
                    )
                    updateBookUseCase.invoke(updatedBook)
                }
                
                hideEditView() // UI 상태 변경은 작업 완료 후
            } catch (e: Exception) {
                handleEvent(BookDetailEvent.ShowError(e.message ?: "알 수 없는 에러가 발생했습니다."))
            }
        }
    }

}