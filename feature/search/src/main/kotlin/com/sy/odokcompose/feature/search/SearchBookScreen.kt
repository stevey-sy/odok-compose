package com.sy.odokcompose.feature.search

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.SubcomposeAsyncImage
import com.sy.odokcompose.core.designsystem.OdokColors
import com.sy.odokcompose.core.designsystem.OdokTheme
import com.sy.odokcompose.model.SearchBookUiModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SearchBookScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (isbn: String, cover: String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val searchPagingFlow by viewModel.searchPagingData.collectAsState()
    val searchResults = searchPagingFlow.collectAsLazyPagingItems()
    val focusManager = LocalFocusManager.current

    // 검색어 입력 후 0.5초 후 자동 검색
    var isFirstLaunch by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        snapshotFlow { query }
            .debounce(500)
            .distinctUntilChanged()
            .collectLatest { searchText ->
                if (searchText.isNotBlank() && !isFirstLaunch) {
                    viewModel.search(searchText)
                }
                isFirstLaunch = false
            }
    }
    
    OdokTheme {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(OdokColors.LightGray)
            ) {
                TextField(
                    value = query,
                    onValueChange = {
                        viewModel.updateSearchQuery(it) 
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    placeholder = { Text("책 제목이나 작가를 검색하세요") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "검색"
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = {
                                viewModel.updateSearchQuery("") 
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "지우기"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            viewModel.search(query)
                            focusManager.clearFocus()
                        }
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                when {
                    uiState.isSearching -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    uiState.errorMessage != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("오류: ${uiState.errorMessage}")
                        }
                    }
                    searchResults.loadState.refresh is LoadState.Error -> {
                        val error = searchResults.loadState.refresh as LoadState.Error
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("검색 실패: ${error.error.localizedMessage}")
                        }
                    }
                    query.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("검색어를 입력하세요")
                        }
                    }
                    uiState.hasSearched && searchResults.itemCount == 0 && searchResults.loadState.refresh !is LoadState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("검색 결과가 없습니다")
                        }
                    }
                    uiState.hasSearched -> {
                        SearchResultsList(
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            searchResults = searchResults,
                            onBookClick = { book ->
                                onNavigateToDetail(book.isbn, book.cover)
                            },

                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SearchResultsList(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    searchResults: LazyPagingItems<SearchBookUiModel>,
    onBookClick: (SearchBookUiModel) -> Unit,

) {
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .background(OdokColors.LightGray)
    ) {
        items(
            count = searchResults.itemCount,
            key = searchResults.itemKey { it.id }
        ) { index ->
            val book = searchResults[index]
            book?.let {
                BookItem(
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    book = it,
                    onClick = { onBookClick(it) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        
        item {
            when (searchResults.loadState.append) {
                is LoadState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
                is LoadState.Error -> {
                    val error = searchResults.loadState.append as LoadState.Error
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("추가 데이터 로드 실패: ${error.error.localizedMessage}")
                    }
                }
                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun BookItem(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    book: SearchBookUiModel,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(140.dp)
            .clickable(onClick = onClick)
    ) {
        // 전체 초록색 배경
        Box(
            modifier = Modifier
                .height(100.dp)
                .align(Alignment.BottomStart)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 108.dp, top = 10.dp, end = 8.dp) // 이미지 너비 + 여유 padding
                    .align(Alignment.TopStart),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        lineHeight = 16.sp // 원하는 행간 값으로 설정 (예: 텍스트 크기보다 약간 크거나 동일하게)
                    ),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )

                Text(
                    text = book.getAuthorText(),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = book.publisher,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
        with(sharedTransitionScope) {
            SubcomposeAsyncImage(
                model = book.cover,
                contentDescription = null,
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(key = "image/${book.cover}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = {initial, taget -> tween(durationMillis = 1000)}
                    )
                    .size(width = 90.dp, height = 140.dp)
                    .padding(start= 10.dp, bottom= 10.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(4.dp),
                        clip = false
                    )
                    .align(Alignment.TopStart)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .size(width = 90.dp, height = 140.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier
                            .size(width = 100.dp, height = 135.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "이미지 로드 실패"
                        )
                    }
                }
            )
        }
    }
}