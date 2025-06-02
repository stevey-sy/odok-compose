package com.sy.odokcompose.feature.mylibrary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sy.odokcompose.core.domain.GetMyBooksUseCase
import com.sy.odokcompose.model.BookUiModel
import com.sy.odokcompose.model.type.ShelfFilterType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MyLibraryViewModel @Inject constructor(
    private val getMyBooksUseCase : GetMyBooksUseCase,
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
        } else {
            _filteredItems.value = _shelfItems.value.filter { book ->
                book.title.lowercase().contains(query) ||
                book.author.lowercase().contains(query)
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
            _filteredItems.value = _shelfItems.value
        }
    }

    private fun getShelfItems(filter: ShelfFilterType = ShelfFilterType.NONE) {
        viewModelScope.launch {
            getMyBooksUseCase(filter).collect { books ->
                val updatedBooks = updateShelfPosition(books)
                val paddedBooks = addDummyItems(updatedBooks)
                _shelfItems.value = paddedBooks
                _filteredItems.value = paddedBooks
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
}

data class MyLibraryUiState(
    val isLoading: Boolean = false,
    val isSearchViewShowing: Boolean = false,
    val errorMessage: String? = null
)