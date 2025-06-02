package com.sy.odokcompose.feature.mylibrary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sy.odokcompose.core.domain.GetBookDetailUseCase
import com.sy.odokcompose.core.domain.GetMyBooksUseCase
import com.sy.odokcompose.model.BookUiModel
import com.sy.odokcompose.model.type.ShelfFilterType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val getMyBooksUseCase: GetMyBooksUseCase,
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

    private fun getShelfItems(filter: ShelfFilterType = ShelfFilterType.NONE,
                              searchQuery: String = "") {
        viewModelScope.launch {
            getMyBooksUseCase(filter).collect { books ->
                _bookList.value = books
            }
        }
    }

    init {
        // navArgs로 전달된 itemId 가져오기
        val itemId: Int? = savedStateHandle["itemId"]
        val filterTypeInt: Int? = savedStateHandle["filterType"]
        val filterType = filterTypeInt?.let { ShelfFilterType.fromCode(it) } ?: ShelfFilterType.NONE
        val searchQuery: String? = savedStateHandle["searchQuery"] ?: ""
        loadBooksAndSetPage(itemId, filterType, searchQuery)
    }

    private fun loadBooksAndSetPage(itemId: Int?,
                                    filterType: ShelfFilterType = ShelfFilterType.NONE,
                                    searchQuery: String?) {
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
                val index = filteredBooks.indexOfFirst { it.itemId == itemId }
                _currentPage.value = if (index >= 0) index else 0
            }
        }
    }

    // ViewPager에서 페이지 변경 시 호출
    fun onPageChanged(newIndex: Int) {
        _currentPage.value = newIndex
    }


}

data class BookDetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)