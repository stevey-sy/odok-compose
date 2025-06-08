package com.sy.odokcompose.feature.mylibrary

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sy.odokcompose.core.designsystem.OdokColors
import com.sy.odokcompose.core.designsystem.OdokTheme
import com.sy.odokcompose.feature.mylibrary.components.BookActionButtons
import com.sy.odokcompose.feature.mylibrary.components.BookEditBottomSheet
import com.sy.odokcompose.feature.mylibrary.components.BookInfo
import com.sy.odokcompose.feature.mylibrary.components.BookPager
import com.sy.odokcompose.feature.mylibrary.components.BookProgress
import com.sy.odokcompose.feature.mylibrary.components.CommentSection
import com.sy.odokcompose.feature.mylibrary.components.MemoListBottomSheet

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun BookDetailScreen(
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onReadBtnClicked: (itemId: Int) -> Unit,
    viewModel: BookDetailViewModel = hiltViewModel(),
) {
    val bookList by viewModel.bookList.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val currentBook by viewModel.currentBook.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val finishedReadCnt by viewModel.finishedReadCnt.collectAsState()
    val currentPageCnt by viewModel.currentPageCnt.collectAsState()
    val memoList by viewModel.memoList.collectAsState()


    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val memoSheetState = rememberModalBottomSheetState()

    // SavedStateHandle 데이터 변경 감지
    LaunchedEffect(Unit) {
        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
        val page = savedStateHandle?.get<Int>("lastReadPage")
        val elapsedTime = savedStateHandle?.get<Int>("elapsedTimeSeconds")
        val isBookReadingCompleted = savedStateHandle?.get<Boolean>("isBookReadingCompleted")
        
        if (page != null && elapsedTime != null && isBookReadingCompleted != null
            && page >= 0 && elapsedTime >= 0) {
            viewModel.updateBookWithTimerData(page, elapsedTime, isBookReadingCompleted)
            // 데이터 처리 후 초기화
            savedStateHandle["lastReadPage"] = null
            savedStateHandle["elapsedTimeSeconds"] = null
            savedStateHandle["isBookReadingCompleted"] = null
        }
    }

    OdokTheme {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(OdokColors.White)
                    .padding(innerPadding)
            ) {

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    BookPager(
                        bookList = bookList,
                        currentPage = currentPage,
                        onPageChanged = viewModel::onPageChanged,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )

                    BookInfo(
                        title = currentBook?.getTitleText() ?: "",
                        author = currentBook?.author ?: ""
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BookProgress(
                        progressPercentage = currentBook?.progressPercentage ?: 0,
                        progressText = currentBook?.progressText ?: ""
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BookActionButtons(
                        onReadBtnClicked= {
                            onReadBtnClicked(currentBook?.itemId ?: 0)
                        }
                    )

                    CommentSection(
                        onCommentsClick = {
                            viewModel.showMemoListView()
                                          },
                        commentCount = memoList.size
                    )
                }

                if (uiState.isEditViewShowing) {
                    BookEditBottomSheet(
                        onDismissRequest = { viewModel.hideEditView() },
                        sheetState = sheetState,
                        finishedReadCnt = finishedReadCnt,
                        currentPageCnt = currentPageCnt,
                        totalPageCnt = currentBook?.totalPageCnt ?: 0,
                        onFinishedReadCntChange = viewModel::updateFinishedReadCnt,
                        onCurrentPageCntChange = viewModel::updateCurrentPageCnt,
                        onSaveClick = viewModel::saveChanges
                    )
                }

                if(uiState.isMemoListShowing) {
                    MemoListBottomSheet(
                        sheetState = memoSheetState,
                        memoList = memoList,
                        onDismissRequest = {viewModel.hideMemoListView()},
                        onEditClick = {},
                        onDeleteClick = {}
                    )
                }
            }
        }
    }
}
