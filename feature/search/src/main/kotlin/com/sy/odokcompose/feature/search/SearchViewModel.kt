package com.sy.odokcompose.feature.search

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun search() {
        // 실제 검색 로직은 나중에 구현
        _uiState.value = _uiState.value.copy(
            isSearching = true
        )
        
        // 검색 후
        _uiState.value = _uiState.value.copy(
            isSearching = false
        )
    }
}

data class SearchUiState(
    val isSearching: Boolean = false,
    val errorMessage: String? = null,
    val searchResults: List<SearchResult> = emptyList()
)

data class SearchResult(
    val id: String = "",
    val title: String = "",
    val author: String = ""
)