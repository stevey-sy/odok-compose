package com.sy.odokcompose.feature.search

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.sy.odokcompose.core.designsystem.OdokTheme
import com.sy.odokcompose.core.designsystem.component.OdokTopAppBar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SearchBookDetailScreen(
    isbn: String,
    cover: String,
    onNavigateBack: () -> Unit,
    viewModel: SearchDetailViewModel = hiltViewModel(),
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    // 책 상세 정보를 로드하기 위해 isbn 전달
    LaunchedEffect(isbn) {
        viewModel.loadBookDetail(isbn)
    }
    
    val uiState by viewModel.uiState.collectAsState()

    OdokTheme {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                Button(
                    onClick = { /* TODO: 저장 로직 구현 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    )
                ) {
                    Text(
                        text = "저장하기",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    uiState.error != null -> {
                        Text(
                            text = "오류가 발생했습니다: ${uiState.error}",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        )
                    }
                    uiState.getBookDetailSuccess != null -> {
                        val bookDetail = uiState.getBookDetailSuccess!!
                        
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            // 책 표지 이미지
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                with(sharedTransitionScope) {
                                    SubcomposeAsyncImage(
                                        model = cover,
                                        contentDescription = "책 표지",
                                        modifier = Modifier
                                            .sharedElement(
                                                rememberSharedContentState(key = "image/$cover"),
                                                animatedVisibilityScope = animatedVisibilityScope,
//                                                boundsTransform = {initial, taget -> tween(durationMillis = 5000)}
                                            )
                                            .size(width = 130.dp, height = 190.dp)
                                            .shadow(
                                                elevation = 8.dp,
                                                shape = RoundedCornerShape(4.dp),
                                                clip = false
                                            )
                                            .clip(RoundedCornerShape(4.dp)),
                                        contentScale = ContentScale.Crop,
                                        loading = {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp)
                                            )
                                        },
                                        error = {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "이미지 로드 실패"
                                            )
                                        }
                                    )
                                }
                            }

                            // 별점
                            Text(
                                text = "별점",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Row {
                                    repeat(5) { index ->
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = "별점",
                                            tint = Color.Black,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            androidx.compose.material3.Divider()
                            Spacer(modifier = Modifier.height(16.dp))

                            // 책 제목
                            BookDetailItem(header = "책 제목", content = bookDetail.title)
                            // 저자
                            BookDetailItem(header = "저자", content = bookDetail.author)
                            // 출판사
                            BookDetailItem(header = "출판사", content = bookDetail.publisher)
                            // ISBN
                            BookDetailItem(header = "ISBN", content = bookDetail.isbn)
                            // 전체 페이지 수
                            BookDetailItem(header = "전체 페이지 수", content = bookDetail.page.toString())
                            // 책 설명
                            BookDetailItem(header = "책 소개", content = bookDetail.description)

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookDetailItem(header: String, content: String) {
    Text(
        text = header,
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Gray
    )
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = content,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))
    androidx.compose.material3.Divider()
    Spacer(modifier = Modifier.height(16.dp))
}