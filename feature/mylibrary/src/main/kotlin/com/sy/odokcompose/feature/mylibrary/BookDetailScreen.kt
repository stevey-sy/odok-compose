package com.sy.odokcompose.feature.mylibrary

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.sy.odokcompose.core.designsystem.DashiFont
import com.sy.odokcompose.core.designsystem.MaruBuriFont
import com.sy.odokcompose.core.designsystem.OdokTheme
import com.sy.odokcompose.core.designsystem.OdokColors
import com.sy.odokcompose.core.designsystem.R
import com.sy.odokcompose.feature.mylibrary.components.*
import androidx.compose.foundation.lazy.items

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
                    ModalBottomSheet(
                        onDismissRequest = {viewModel.hideMemoListView()},
                        sheetState = memoSheetState,
                    ) {
                        LazyColumn (
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            items(memoList) { memo ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 20.dp, start=24.dp, end=24.dp, bottom=20.dp)
                                        .wrapContentHeight()
                                        .shadow(8.dp, RoundedCornerShape(10.dp))
                                        .background(Color.White, RoundedCornerShape(10.dp))
                                ) {
                                    Image(
                                        painter = painterResource(id = memo.backgroundId),
                                        contentDescription = "메모하기",
                                        contentScale = ContentScale.FillBounds,
                                        modifier = Modifier.matchParentSize()
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 24.dp, top=24.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "p.",
                                            fontSize = 16.sp,
                                            fontFamily = MaruBuriFont,
                                            color = OdokColors.Black
                                        )
                                        Text(
                                            text = memo.pageNumber.toString(),
                                            fontSize = 16.sp,
                                            fontFamily = MaruBuriFont,
                                            color = OdokColors.Black
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 240.dp)
                                            .padding(horizontal = 24.dp, vertical = 20.dp),

                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = memo.content,
                                            style = TextStyle(
                                                color = OdokColors.Black,
                                                fontSize = 22.sp,
                                                fontFamily = DashiFont,
                                                fontWeight = FontWeight.Normal,
                                                textAlign = TextAlign.Center
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .wrapContentHeight()
                                                .align(Alignment.Center)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 20.dp)
                                            .align(Alignment.BottomCenter), // ✅ 날짜는 아래로 고정
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = memo.getCreateDateText(),
                                            fontSize = 14.sp,
                                            fontFamily = MaruBuriFont,
                                            fontWeight = FontWeight.Normal,
                                            color = OdokColors.StealGray,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}