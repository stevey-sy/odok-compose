package com.sy.odokcompose.feature.mylibrary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sy.odokcompose.core.domain.DeleteBookResult
import com.sy.odokcompose.core.domain.DeleteBookUseCase
import com.sy.odokcompose.core.domain.GetMyBooksUseCase
import com.sy.odokcompose.model.BookUiModel
import com.sy.odokcompose.model.type.ShelfFilterType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MyLibraryViewModel @Inject constructor(
    private val getMyBooksUseCase: GetMyBooksUseCase,
    private val deleteBookUseCase: DeleteBookUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(MyLibraryUiState())
    val uiState: StateFlow<MyLibraryUiState> = _uiState.asStateFlow()

    private val _shelfItems = MutableStateFlow<List<BookUiModel>>(emptyList())
    val shelfItems: StateFlow<List<BookUiModel>> = _shelfItems.asStateFlow()

    private val _currentFilter = MutableStateFlow(ShelfFilterType.NONE)
    val currentFilter: StateFlow<ShelfFilterType> = _currentFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredItems = MutableStateFlow<List<BookUiModel>>(emptyList())
    val filteredItems: StateFlow<List<BookUiModel>> = _filteredItems.asStateFlow()

    init {
        getShelfItems()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterItems()
    }

    private fun filterItems() {
        val query = _searchQuery.value.lowercase()
        if (query.isEmpty()) {
            _filteredItems.value = _shelfItems.value
            _uiState.update { it.copy(
                noMatchingBooksMessage = null,
                isSearchResult = false
            ) }
        } else {
            _filteredItems.value = _shelfItems.value.filter { book ->
                book.title.lowercase().contains(query) ||
                book.author.lowercase().contains(query)
            }
            if (_filteredItems.value.isEmpty()) {
                _uiState.update { it.copy(
                    noMatchingBooksMessage = "'$query'에 대한 검색 결과가 없습니다",
                    isSearchResult = true
                ) }
            } else {
                _uiState.update { it.copy(
                    noMatchingBooksMessage = null,
                    isSearchResult = false
                ) }
            }
        }
    }

    fun updateFilter(filter: ShelfFilterType) {
        _currentFilter.value = filter
        getShelfItems(filter)
    }

    fun toggleSearchView(show: Boolean) {
        _uiState.value = _uiState.value.copy(isSearchViewShowing = show)
        if (!show) {
            _searchQuery.value = ""
//            _filteredItems.value = _shelfItems.value
            filterItems()
        }
    }

    private fun getShelfItems(filter: ShelfFilterType = ShelfFilterType.NONE) {
        viewModelScope.launch {
            getMyBooksUseCase(filter).collect { books ->
                val updatedBooks = updateShelfPosition(books)
                val paddedBooks = addDummyItems(updatedBooks)
                _shelfItems.value = paddedBooks
                _filteredItems.value = paddedBooks
                
                if (books.isEmpty()) {
                    _uiState.update { it.copy(
                        isEmptyLibrary = filter == ShelfFilterType.NONE,
                        noMatchingBooksMessage = when (filter) {
                            ShelfFilterType.READING -> "읽고 있는 책이 없습니다"
                            ShelfFilterType.FINISHED -> "완독한 책이 없습니다"
                            else -> null
                        },
                        isSearchResult = false
                    ) }
                } else {
                    _uiState.update { it.copy(
                        isEmptyLibrary = false,
                        noMatchingBooksMessage = null,
                        isSearchResult = false
                    ) }
                }
            }
        }
    }

    private fun updateShelfPosition(originalList: List<BookUiModel>): List<BookUiModel> {
        return originalList.mapIndexed { index, book ->
            book.copy(shelfPosition = index % 3)
        }
    }

    private fun addDummyItems(originalList: List<BookUiModel>): List<BookUiModel> {
        val remainder = originalList.size % 3
        return if (remainder == 0) {
            originalList
        } else {
            val paddingCount = 3 - remainder
            val padded = originalList.toMutableList()
            repeat(paddingCount) {
                padded.add(
                    BookUiModel(
                        itemId = -1 * (it + 1),
                    )
                )
            }
            padded
        }
    }

    fun toggleDeleteMode() {
        _uiState.update { it.copy(
            isDeleteMode = !it.isDeleteMode,
            selectedItems = emptySet()
        ) }
    }

    fun toggleItemSelection(itemId: Int) {
        _uiState.update { currentState ->
            val newSelectedItems = currentState.selectedItems.toMutableSet()
            if (newSelectedItems.contains(itemId)) {
                newSelectedItems.remove(itemId)
            } else {
                newSelectedItems.add(itemId)
            }
            currentState.copy(selectedItems = newSelectedItems)
        }
    }

    fun deleteSelectedItems() {
        viewModelScope.launch {
            val result = deleteBookUseCase.invokeMultiple(uiState.value.selectedItems)
            when (result) {
                is DeleteBookResult.Success -> {
                    // 삭제 성공 후 목록 새로고침
                    getShelfItems(currentFilter.value)
                    // 삭제 모드 종료
                    _uiState.update { it.copy(
                        isDeleteMode = false,
                        selectedItems = emptySet(),
                        snackBarMessage = "${uiState.value.selectedItems.size}개의 책이 삭제되었습니다."
                    ) }
                }
                is DeleteBookResult.Error -> {
                    // 에러 처리
                    _uiState.update { it.copy(
                        errorMessage = result.message,
                        snackBarMessage = result.message
                    ) }
                }
            }
        }
    }

    fun clearSnackBarMessage() {
        _uiState.update { it.copy(snackBarMessage = null) }
    }
}

data class MyLibraryUiState(
    val isLoading: Boolean = false,
    val isSearchViewShowing: Boolean = false,
    val isDeleteMode: Boolean = false,
    val selectedItems: Set<Int> = emptySet(),
    val errorMessage: String? = null,
    val snackBarMessage: String? = null,
    val isEmptyLibrary: Boolean = false,
    val noMatchingBooksMessage: String? = null,
    val isSearchResult: Boolean = false
)