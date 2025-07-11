package com.sy.odokcompose.feature.mylibrary

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sy.odokcompose.core.designsystem.OdokTheme
import com.sy.odokcompose.core.designsystem.icon.OdokIcons
import com.sy.core.ui.components.BookCover
import com.sy.odokcompose.feature.mylibrary.components.BookShelfBase
import com.sy.odokcompose.feature.mylibrary.components.BookShelfLeft
import com.sy.odokcompose.feature.mylibrary.components.BookShelfRight
import com.sy.odokcompose.model.BookUiModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import com.sy.core.ui.components.SearchTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.platform.LocalConfiguration
import com.sy.odokcompose.core.designsystem.OdokColors
import com.sy.odokcompose.model.type.ShelfFilterType


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MyLibraryScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigateToSearch: () -> Unit,
    onBookItemClicked: (itemId: Int, filterType: Int, searchQuery: String) -> Unit,
    viewModel: MyLibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredItems by viewModel.filteredItems.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // SnackBar 메시지 처리
    LaunchedEffect(uiState.snackBarMessage) {
        uiState.snackBarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackBarMessage()
        }
    }

    // 검색창이 열려있으면 BackHandler 활성화
    if (uiState.isSearchViewShowing) {
        BackHandler {
            viewModel.toggleSearchView(false)  // ViewModel의 검색창 닫기 로직 호출
        }
    } else if (uiState.isDeleteMode) {
        BackHandler {
            viewModel.toggleDeleteMode()
        }
    }

    OdokTheme {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                AnimatedVisibility(
                    visible = uiState.isDeleteMode,
                    enter = expandVertically(
                        animationSpec = tween(300)
                    ) + fadeIn(
                        animationSpec = tween(300)
                    ),
                    exit = shrinkVertically(
                        animationSpec = tween(300)
                    ) + fadeOut(
                        animationSpec = tween(300)
                    )
                ) {
                    TopAppBar(
                        title = { Text("${uiState.selectedItems.size}개 선택됨") },
                        actions = {
                            TextButton(
                                onClick = { viewModel.deleteSelectedItems() }
                            ) {
                                Text("삭제")
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { viewModel.toggleDeleteMode() }) {
                                Icon(Icons.Default.Close, contentDescription = "닫기")
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier.animateContentSize()
                ) {
                    AnimatedVisibility(
                        visible = currentFilter != ShelfFilterType.NONE,
                        enter = expandVertically(
                            animationSpec = tween(300)
                        ) + fadeIn(
                            animationSpec = tween(300)
                        ),
                        exit = shrinkVertically(
                            animationSpec = tween(300)
                        ) + fadeOut(
                            animationSpec = tween(300)
                        )
                    ) {
                        FilterStatusView(
                            filterType = currentFilter,
                            onClearFilter = { viewModel.updateFilter(ShelfFilterType.NONE) }
                        )
                    }

                    AnimatedVisibility(
                        visible = uiState.isSearchViewShowing,
                        enter = expandVertically(
                            animationSpec = tween(300)
                        ) + fadeIn(
                            animationSpec = tween(300)
                        ),
                        exit = shrinkVertically(
                            animationSpec = tween(300)
                        ) + fadeOut(
                            animationSpec = tween(300)
                        )
                    ) {
                        SearchTextField(
                            query = searchQuery,
                            focusManager = focusManager,
                            onKeyTypes = { viewModel.updateSearchQuery(it) },
                            onSearchClicked = { viewModel.updateSearchQuery(it) },
                            onClose = { viewModel.toggleSearchView(false) }
                        )
                    }

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(
                            animationSpec = tween(300)
                        ),
                        exit = fadeOut(
                            animationSpec = tween(300)
                        )
                    ) {
                        when {
                            uiState.isEmptyLibrary -> {
                                EmptyLibraryView(onNavigateToSearch)
                            }
                            uiState.noMatchingBooksMessage != null -> {
                                val message = uiState.noMatchingBooksMessage
                                NoMatchingBooksView(
                                    message = message.toString(),
                                    isSearchResult = uiState.isSearchResult,
                                    onClearSearch = { viewModel.updateSearchQuery("") }
                                )
                            }
                            else -> {
                                val gridState = rememberLazyGridState()
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    state = gridState,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    content = {
                                        itemsIndexed(filteredItems) { index, book ->
                                            BookShelfItem(
                                                sharedTransitionScope = sharedTransitionScope,
                                                animatedVisibilityScope = animatedVisibilityScope,
                                                index = index,
                                                book = book,
                                                isLastItem = index == filteredItems.lastIndex,
                                                onClick = { itemId ->
                                                    onBookItemClicked(
                                                        itemId,
                                                        currentFilter.code,
                                                        searchQuery
                                                    )
                                                },
                                                onLongClick = { viewModel.toggleDeleteMode() },
                                                isDeleteMode = uiState.isDeleteMode,
                                                isSelected = uiState.selectedItems.contains(book.itemId),
                                                onSelectionChanged = { isSelected ->
                                                    viewModel.toggleItemSelection(book.itemId)
                                                }
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun BookShelfItem(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    index: Int,
    book: BookUiModel,
    isLastItem: Boolean = false,
    onClick: ((itemId: Int) -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    isDeleteMode: Boolean = false,
    isSelected: Boolean = false,
    onSelectionChanged: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    // 화면 크기에 따라 높이 계산
    val coverHeight = when {
        screenWidth <= 360.dp -> 140.dp  // 작은 화면
        screenWidth < 400.dp -> 150.dp  // 중간 화면
        else -> 160.dp                  // 큰 화면
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .aspectRatio(0.7f)
    ) {
        BookShelfComponent(
            index = index,
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .align(Alignment.BottomCenter)
        )

        if (book.itemId > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .alpha(if (isDeleteMode && !isSelected) 0.5f else 1f)
            ) {
                BookCover(
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    book = book,
                    modifier = Modifier
                        .height(coverHeight)
                        .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 25.dp)
                        .combinedClickable(
                            enabled = onClick != null,
                            onClick = {
                                if (isDeleteMode) {
                                    onSelectionChanged?.invoke(!isSelected)
                                } else {
                                    onClick?.invoke(book.itemId)
                                }
                            },
                            onLongClick = onLongClick
                        )
                )
                
                if (isDeleteMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = onSelectionChanged,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                    )
                }
            }
        } else if (isLastItem) {
            Image(
                painter = painterResource(id = OdokIcons.Plant),
                contentDescription = "식물",
                modifier = Modifier
                    .width(65.dp)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 19.dp)
            )
        }
    }
}

@Composable
private fun BookShelfComponent(
    index: Int,
    modifier: Modifier = Modifier
) {
    when (index % 3) {
        0 -> BookShelfLeft(modifier = modifier)
        1 -> BookShelfBase(modifier = modifier)
        2 -> BookShelfRight(modifier = modifier)
    }
}

@Composable
private fun EmptyLibraryView(onNavigateToSearch: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = OdokIcons.EmptyLibrary),
                contentDescription = "비어있는 서재",
                modifier = Modifier
                    .fillMaxWidth()
            )
            Text(
                text = "당신의 첫 번째 책을 추가해 보세요",
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 32.dp)
            )
            Text(
                text = "독서 진행도와 기억하고 싶은 문구를 기록할 수 있어요",
                style = typography.bodyMedium,
                color = colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 8.dp)
            )
            Button(
                onClick = onNavigateToSearch,
                modifier = Modifier.padding(top = 22.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(
                    text = "책 검색하기",
                )
            }
        }
    }
}

@Composable
private fun NoMatchingBooksView(
    message: String,
    isSearchResult: Boolean,
    onClearSearch: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = OdokIcons.Plant),
                contentDescription = "검색 결과 없음",
                modifier = Modifier
                    .fillMaxWidth()
            )
            Text(
                text = "조건과 일치하는 책이 없습니다",
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 32.dp)
            )
            Text(
                text = message,
                style = typography.bodyMedium,
                color = colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 8.dp)
            )
            if (isSearchResult) {
                TextButton(
                    onClick = onClearSearch,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("검색어 지우기")
                }
            }
        }
    }
}

@Composable
private fun FilterStatusView(
    filterType: ShelfFilterType,
    onClearFilter: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(
                color = OdokColors.Black,
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (filterType) {
                    ShelfFilterType.READING -> "읽고 있는 책만 보여요."
                    ShelfFilterType.FINISHED -> "완독한 책만 보여요."
                    else -> ""
                },
                style = typography.bodyMedium,
                color = OdokColors.White
            )
            IconButton(
                onClick = onClearFilter,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "필터 해제",
                    tint = OdokColors.White
                )
            }
        }
    }
}
