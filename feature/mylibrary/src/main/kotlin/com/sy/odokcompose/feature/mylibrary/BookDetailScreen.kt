package com.sy.odokcompose.feature.mylibrary

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sy.odokcompose.core.designsystem.OdokTheme
import com.sy.odokcompose.core.designsystem.icon.OdokIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    // 실제 데이터는 파라미터로 받아오면 됩니다.
) {
    OdokTheme {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // 상단 ViewPager (책 표지 여러 장일 경우)
                val pagerState = rememberPagerState(pageCount = { 1 }) // 표지 여러 장이면 숫자 변경
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) { page ->
                    // 임시로 painterResource 사용, 실제로는 이미지 url 등으로 대체
                    Image(
                        painter = painterResource(id = OdokIcons.Plant),
                        contentDescription = "책 표지",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 책 제목
                Text(
                    text = "괜찮은 어른이 되고 싶어서",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // 지은이
                Text(
                    text = "봉태규",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(24.dp))

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