package com.sy.odokcompose.feature.mylibrary

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sy.odokcompose.core.designsystem.OdokTheme
import com.sy.odokcompose.core.designsystem.icon.OdokIcons
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.sy.odokcompose.core.designsystem.OdokColors
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun BookDetailScreen(
    viewModel: BookDetailViewModel = hiltViewModel(),
) {
    val bookList by viewModel.bookList.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val currentBook by viewModel.currentBook.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    OdokTheme {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(innerPadding)
            ) {

                val pagerState = if (bookList.isNotEmpty()) {
                    rememberPagerState(
                        initialPage = currentPage,
                        pageCount = { bookList.size }
                    )
                } else {
                    rememberPagerState(
                        initialPage = 0,
                        pageCount = { 0 }
                    )
                }

                LaunchedEffect(pagerState.currentPage) {
                    if (pagerState.currentPage != currentPage) {
                        viewModel.onPageChanged(pagerState.currentPage)
                    }
                }

                val configuration = LocalConfiguration.current
                val screenWidth = configuration.screenWidthDp.dp   // 기기별 화면 너비
                val itemWidth = 200.dp                             // 아이템 너비
                val horizontalPadding = (screenWidth - itemWidth) / 2

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentPadding = PaddingValues(horizontal = horizontalPadding), // 좌우 여백 추가
                ) { page ->
                    val book = bookList.getOrNull(page)
                    if (book != null) {
                        Box(
                            modifier = Modifier
                                .width(200.dp)
                                .height(280.dp)
                                .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 25.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(10.dp)   // 여기서 radius를 16dp로 지정
                                )
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(10.dp)   // background도 동일하게 radius를 적용
                                )
                        ) {
                            SubcomposeAsyncImage(
                                model = book.coverImageUrl,
                                contentDescription = book.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                }

                // 책 제목
                Text(
                    text = currentBook?.title ?: "",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal= 40.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // 지은이
                Text(
                    text = currentBook?.author ?: "",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top= 6.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = OdokColors.StealGray,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(24.dp))
                SimpleSpeechBubble(currentBook?.getPercentageStr() ?: "",
                    (currentBook?.progressPercentage?.times(0.01f)) ?: 0f
                )
                // 독서 진행도 progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .padding(horizontal = 40.dp)
                ) {
                    // 배경 (회색)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(
                                color = Color.LightGray,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .align(Alignment.BottomStart)
                    )

                    val animatedProgress by animateFloatAsState(
                        targetValue = (currentBook?.progressPercentage?.times(0.01f)) ?: 0f,
                        animationSpec = tween(durationMillis = 700),
                        label = "progressBar"
                    )

                    // 진행도 (검정색)
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .fillMaxWidth(animatedProgress.coerceAtLeast(0.001f))
                            .background(
                                color = Color.Black,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .align(Alignment.BottomStart)
                    )
                }

                // progress Text
                Text(
                    text = currentBook?.progressText ?: "",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top= 6.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = OdokColors.StealGray,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 버튼 영역
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { /* 독서 버튼 클릭 */ }) {
                        Text("독서")
                    }
                    Button(onClick = { /* 기록 버튼 클릭 */ }) {
                        Text("기록")
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleSpeechBubble(
    text: String,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val boxWidth = LocalConfiguration.current.screenWidthDp.dp - 80.dp // 40.dp padding on each side
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 700),
        label = "progress"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .offset(x = (boxWidth * animatedProgress + 20.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Black, RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = text,
                    fontSize = 11.sp,
                    color = Color.White)
            }
            // 꼬리 부분
            Canvas(
                modifier = Modifier
                    .size(10.dp, 6.dp)
            ) {
                drawPath(
                    path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width / 2, size.height)
                        close()
                    },
                    color = Color.Black
                )
            }
        }
    }
}