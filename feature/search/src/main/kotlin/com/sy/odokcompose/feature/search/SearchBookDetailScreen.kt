package com.sy.odokcompose.feature.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.sy.odokcompose.core.designsystem.OdokTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBookDetailScreen(
    isbn: String,
    cover: String,
    onNavigateBack: () -> Unit,
    viewModel: SearchDetailViewModel = hiltViewModel()
) {
    // 책 상세 정보를 로드하기 위해 isbn 전달
    LaunchedEffect(isbn) {
        viewModel.loadBookDetail(isbn)
    }
    
    val uiState by viewModel.uiState.collectAsState()

    OdokTheme {
        Scaffold(
//            topBar = {
//                TopAppBar(
//                    title = { Text("책 상세 정보") },
//                    navigationIcon = {
//                        IconButton(onClick = onNavigateBack) {
//                            Icon(
//                                imageVector = Icons.Default.ArrowBack,
//                                contentDescription = "뒤로 가기"
//                            )
//                        }
//                    }
//                )
//            }
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
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
                    uiState.bookDetail != null -> {
                        val bookDetail = uiState.bookDetail!!
                        
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
                                SubcomposeAsyncImage(
                                    model = cover,
                                    contentDescription = "책 표지",
                                    modifier = Modifier
                                        .size(width = 160.dp, height = 240.dp)
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
                            
                            // 책 제목
                            Text(
                                text = bookDetail.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // 저자
                            Text(
                                text = "저자: ${bookDetail.author}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // 출판사
                            Text(
                                text = "출판사: ${bookDetail.publisher}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // ISBN
                            Text(
                                text = "ISBN: $isbn",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // 책 설명
                            Text(
                                text = "책 소개",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = bookDetail.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
        }
    }
        }
    }
}