package com.sy.odokcompose.feature.mylibrary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sy.odokcompose.core.domain.GetBookDetailUseCase
import com.sy.odokcompose.core.domain.GetMyBooksUseCase
import com.sy.odokcompose.core.domain.UpdateBookUseCase
import com.sy.odokcompose.model.BookUiModel
import com.sy.odokcompose.model.type.ShelfFilterType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val getMyBooksUseCase: GetMyBooksUseCase,
    private val updateBookUseCase: UpdateBookUseCase,
    private val savedStateHandle : SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState : StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    private val _bookList = MutableStateFlow<List<BookUiModel>>(emptyList())
    val bookList: StateFlow<List<BookUiModel>> = _bookList.asStateFlow()

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
    private var activeItemId: Int? = null

    init {
        // navArgs로 전달된 itemId 가져오기
        val itemIdFromNav: Int? = savedStateHandle["itemId"]
        activeItemId = itemIdFromNav // 초기 activeItemId 설정

        val filterTypeInt: Int? = savedStateHandle["filterType"]
        val filterType = filterTypeInt?.let { ShelfFilterType.fromCode(it) } ?: ShelfFilterType.NONE
        val searchQuery: String? = savedStateHandle["searchQuery"] ?: ""
        loadBooksAndSetPage(filterType, searchQuery)
    }

    private fun loadBooksAndSetPage(
        filterType: ShelfFilterType = ShelfFilterType.NONE,
        searchQuery: String?
    ) {
        viewModelScope.launch {
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
                if (activeItemId != null) {
                    val indexByActiveItemId = filteredBooks.indexOfFirst { it.itemId == activeItemId }
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
                            // 이 경우, activeItemId도 NavArgs의 값으로 다시 동기화 고려
                            // (하지만 페이지 이동 후 저장한 상황에서는 activeItemId가 이미 더 최신일 수 있으므로 주의)
                            // activeItemId = initialItemIdFromNav
                        }
                    }
                }
//
                // 3. 그래도 페이지를 결정하지 못했다면, 리스트가 비어있지 않으면 0번째 인덱스 사용
                if (determinedPageIndex == -1 && filteredBooks.isNotEmpty()) {
                    determinedPageIndex = 0
                }

                // 4. 최종적으로 결정된 페이지 인덱스를 _currentPage에 반영
                _currentPage.value = determinedPageIndex

                // 5. 현재 페이지가 유효한 경우, activeItemId를 현재 페이지의 itemId와 동기화
                //    (주로 determinedPageIndex가 0으로 설정되었거나, activeItemId가 유효하지 않았던 경우)
                if (determinedPageIndex != -1 && filteredBooks.indices.contains(determinedPageIndex)) {
                    val currentItemInPager = filteredBooks.getOrNull(determinedPageIndex)
                    activeItemId = currentItemInPager?.itemId
                } else if (filteredBooks.isEmpty()) {
                    activeItemId = null // 리스트가 비면 activeItemId도 초기화
                }
            }
        }
    }

    // ViewPager에서 페이지 변경 시 호출
    fun onPageChanged(newIndex: Int) {
        _currentPage.value = newIndex
        // 페이지가 변경되면 activeItemId도 현재 페이지의 책 ID로 업데이트
        activeItemId = _bookList.value.getOrNull(newIndex)?.itemId
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

    fun updateFinishedReadCnt(value: String) {
        _finishedReadCnt.value = value
    }

    fun updateCurrentPageCnt(value: String) {
        _currentPageCnt.value = value
    }

    fun saveChanges() {
        viewModelScope.launch {
            try {
                val finishedReadCntValue = _finishedReadCnt.value.toIntOrNull() ?: 0
                val currentPageCntValue = _currentPageCnt.value.toIntOrNull() ?: 0
                
                currentBook.value?.let { bookToUpdate ->
                    // 업데이트될 책의 ID를 activeItemId로 설정
                    // 이렇게 하면 _bookList가 갱신된 후 loadBooksAndSetPage의 collect 블록에서
                    // 이 책을 기준으로 _currentPage를 올바르게 설정하려고 시도합니다.
                    activeItemId = bookToUpdate.itemId

                    val updatedBook = bookToUpdate.copy(
                        finishedReadCnt = finishedReadCntValue,
                        currentPageCnt = currentPageCntValue
                    )
                    updateBookUseCase.invoke(updatedBook)
                }
                
                hideEditView() // UI 상태 변경은 작업 완료 후
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

}

data class BookDetailUiState(
    val isLoading: Boolean = false,
    val isEditViewShowing: Boolean = false,
    val isMemoViewShowing: Boolean = false,
    val errorMessage: String? = null
)