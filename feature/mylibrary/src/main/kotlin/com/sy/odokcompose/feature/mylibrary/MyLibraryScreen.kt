package com.sy.odokcompose.feature.mylibrary

import android.widget.ImageView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.sy.odokcompose.core.designsystem.OdokColors
import com.sy.odokcompose.core.designsystem.OdokTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyLibraryScreen(
    onNavigateToSearch: () -> Unit,
    viewModel: MyLibraryViewModel = hiltViewModel()
) {
    val shelfItems by viewModel.shelfItems.collectAsState()
    OdokTheme {
        Scaffold (
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ){ innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    content = {
                        itemsIndexed(shelfItems) { index, book ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(0.7f)
                            ) {
                                // BookShelfBase 분기 처리
                                when (index % 3) {
                                    0 -> BookShelfLeft(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(30.dp)
                                            .align(Alignment.BottomCenter)
                                    )
                                    1 -> BookShelfBase(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(30.dp)
                                            .align(Alignment.BottomCenter)
                                    )
                                    2 -> BookShelfRight(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(30.dp)
                                            .align(Alignment.BottomCenter)
                                    )
                                }

                                // 책 표지 이미지 + 그림자
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .align(Alignment.BottomCenter)
                                        .padding(start = 18.dp, end = 18.dp, top = 10.dp, bottom = 25.dp)
                                        .shadow(10.dp, RectangleShape)
                                        .background(Color.White)
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
                    }
                )

            }
        }
    }
}

@Composable
fun BookShelfBase(
    modifier: Modifier = Modifier,
    colorTopLine: Color = OdokColors.BookShelf,
    colorShelf: Color = OdokColors.BookShelfCenter,
    colorShadow: Color = OdokColors.BookShelfShadow
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // 선반 상단 경계선
        drawRect(
            color = colorTopLine,
            topLeft = Offset(0f, 0f),
            size = Size(width, height * 0.3f) // 높이의 10% 정도
        )

        // 선반 본체 (중앙)
        drawRect(
            color = colorShelf,
            topLeft = Offset(0f, height * 0.3f),
            size = Size(width, height * 0.25f) // 높이의 40% 정도
        )

        // 그림자 효과 (선반 아래)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(colorShadow, Color.White), // 위쪽에서 아래쪽으로 색상 변화
                startY = height * 0.55f,   // 시작 y 위치 (그림자 시작점)
                endY = height              // 끝 y 위치 (그림자 끝점)
            ),
            topLeft = Offset(0f, height * 0.55f),
            size = Size(width, height * 0.45f)
        )
    }
}

@Composable
fun BookShelfLeft(
    modifier: Modifier = Modifier,
    colorTopLine: Color = OdokColors.BookShelf,
    colorShelf: Color = OdokColors.BookShelfCenter,
    colorShadow: Color = OdokColors.BookShelfShadow
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // 선반 상단 경계선
        drawRect(
            color = colorTopLine,
            topLeft = Offset(0f, 0f),
            size = Size(width, height * 0.3f) // 높이의 10% 정도
        )

        // 역삼각형 (왼쪽)
        drawPath(
            path = Path().apply {
                moveTo(0f, 0f)
                lineTo(width * 0.05f, 0f)
                lineTo(0f, height * 0.2f)
                close()
            },
            color = Color.White
        )

        // 선반 본체 (중앙)
        drawRect(
            color = colorShelf,
            topLeft = Offset(0f, height * 0.3f),
            size = Size(width, height * 0.25f) // 높이의 40% 정도
        )

        // 그림자 효과 (선반 아래)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(colorShadow, Color.White), // 위쪽에서 아래쪽으로 색상 변화
                startY = height * 0.55f,   // 시작 y 위치 (그림자 시작점)
                endY = height              // 끝 y 위치 (그림자 끝점)
            ),
            topLeft = Offset(0f, height * 0.55f),
            size = Size(width, height * 0.45f)
        )
    }
}

@Composable
fun BookShelfRight(
    modifier: Modifier = Modifier,
    colorTopLine: Color = OdokColors.BookShelf,
    colorShelf: Color = OdokColors.BookShelfCenter,
    colorShadow: Color = OdokColors.BookShelfShadow
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // 선반 상단 경계선
        drawRect(
            color = colorTopLine,
            topLeft = Offset(0f, 0f),
            size = Size(width, height * 0.3f) // 높이의 10% 정도
        )

        // 역삼각형 (오른쪽)
        drawPath(
            path = Path().apply {
                moveTo(width, 0f)
                lineTo(width * 0.95f, 0f)
                lineTo(width, height * 0.2f)
                close()
            },
            color = Color.White
        )

        // 선반 본체 (중앙)
        drawRect(
            color = colorShelf,
            topLeft = Offset(0f, height * 0.3f),
            size = Size(width, height * 0.25f) // 높이의 40% 정도
        )

        // 그림자 효과 (선반 아래)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(colorShadow, Color.White), // 위쪽에서 아래쪽으로 색상 변화
                startY = height * 0.55f,   // 시작 y 위치 (그림자 시작점)
                endY = height              // 끝 y 위치 (그림자 끝점)
            ),
            topLeft = Offset(0f, height * 0.55f),
            size = Size(width, height * 0.45f)
        )
    }
}
