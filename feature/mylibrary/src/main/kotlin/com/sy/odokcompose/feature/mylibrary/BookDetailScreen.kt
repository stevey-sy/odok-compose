package com.sy.odokcompose.feature.mylibrary

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Offset

import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import com.sy.odokcompose.core.designsystem.icon.OdokIcons

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
                                .fillMaxWidth()
                                .wrapContentHeight(),
                        ) {
                            items(memoList) { memo ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 20.dp, start = 24.dp, end = 24.dp, bottom = 20.dp)
                                        .wrapContentHeight()
                                        .heightIn(min = 240.dp)
                                        .shadow(8.dp, RoundedCornerShape(10.dp))
                                        .background(Color.White, RoundedCornerShape(10.dp))
                                ) {

                                    Image(
                                        painter = painterResource(id = when(memo.backgroundId) {
                                            "white_paper" -> OdokIcons.WhitePaper
                                            "old_paper" -> OdokIcons.OldPaper
                                            "dot_paper" -> OdokIcons.DotPaper
                                            "blue_sky" -> OdokIcons.BlueSky
                                            "yellow_paper" -> OdokIcons.YellowPaper
                                            else -> OdokIcons.WhitePaper
                                        }),
                                        contentDescription = "메모 배경",
                                        contentScale = ContentScale.FillBounds,
                                        modifier = Modifier.matchParentSize()
                                    )

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                            .padding(horizontal = 24.dp, vertical = 20.dp)
                                    ) {
                                        // 상단 페이지 정보
                                        Row {
                                            Text(
                                                text = "p.${memo.pageNumber}",
                                                fontSize = 16.sp,
                                                fontFamily = MaruBuriFont,
                                                color = if (memo.pageNumber == 0) Color.Transparent else Color.Unspecified
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // 중앙 Box: 한 줄이어도 정중앙에 배치되도록
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(min = 120.dp)
                                                .wrapContentHeight(),
                                            contentAlignment = Alignment.Center // 👈 핵심
                                        ) {
                                            Text(
                                                text = memo.content,
                                                fontSize = 22.sp,
                                                fontFamily = DashiFont,
                                                fontWeight = FontWeight.Normal,
                                                textAlign = TextAlign.Center,
                                                color = Color.Black,
                                                softWrap = true,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .wrapContentHeight()
                                                    .padding(horizontal = 16.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(24.dp))

                                        // 하단 날짜 정보
                                        Text(
                                            text = memo.getCreateDateText(),
                                            fontSize = 14.sp,
                                            fontFamily = MaruBuriFont,
                                            color = OdokColors.StealGray,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.TopEnd
                                    ) {
                                        var showMenu by remember { mutableStateOf(false) }
                                        var iconOffset by remember { mutableStateOf(Offset.Zero) }
                                        val density = LocalDensity.current
                                        val iconSize = 24.dp

                                        Box(
                                            modifier = Modifier
                                                .onGloballyPositioned { coordinates ->
                                                    iconOffset = coordinates.positionInWindow()
                                                }
                                        ) {
                                            Image(
                                                painter = painterResource(id = OdokIcons.HorizontalDots),
                                                contentDescription = "메모 설정",
                                                modifier = Modifier
                                                    .size(iconSize)
                                                    .clickable { showMenu = true }
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = showMenu,
                                            onDismissRequest = { showMenu = false },
                                            offset = DpOffset(
                                                x = with(density) { iconOffset.x.toDp() },
                                                y = with(density) { (iconOffset.y + iconSize.toPx()).toDp() }
                                            )
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("수정") },
                                                onClick = {
                                                    showMenu = false
                                                    // TODO
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("삭제") },
                                                onClick = {
                                                    showMenu = false
                                                    // TODO
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
        }
    }
}