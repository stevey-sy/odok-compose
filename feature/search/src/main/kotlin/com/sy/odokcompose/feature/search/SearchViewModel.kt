package com.sy.odokcompose.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sy.odokcompose.core.data.repository.BookRepository
import com.sy.odokcompose.model.SearchBookUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        // 검색어가 비어있을 경우 결과 초기화
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(
                searchResults = emptyList(),
                hasSearched = false
            )
        }
    }
    
    // 현재 검색어로 검색
    fun search() {
        search(_searchQuery.value)
    }
    
    // 주어진 검색어로 검색
    fun search(query: String) {
        if (query.isBlank()) return
        
        _uiState.value = _uiState.value.copy(
            isSearching = true,
            errorMessage = null
        )
        
        viewModelScope.launch {
            try {
                val results = bookRepository.searchBooks(query)
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    searchResults = results,
                    hasSearched = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    errorMessage = e.message,
                    hasSearched = true
                )
            }
        }
    }
}

data class SearchUiState(
    val isSearching: Boolean = false,
    val errorMessage: String? = null,
    val searchResults: List<SearchBookUiModel> = emptyList(),
    val hasSearched: Boolean = false
) 