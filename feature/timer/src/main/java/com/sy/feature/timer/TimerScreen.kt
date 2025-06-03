package com.sy.feature.timer

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sy.odokcompose.core.designsystem.OdokColors
import com.sy.odokcompose.core.designsystem.OdokTheme
import com.sy.odokcompose.core.designsystem.component.BookCover
import com.sy.odokcompose.core.designsystem.icon.OdokIcons

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TimerScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClose: () -> Unit = {},
    viewModel: TimerViewModel = hiltViewModel()
) {
    val book by viewModel.book.collectAsState()

    OdokTheme {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Close Button (X)
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            modifier = Modifier.size(80.dp),
                            contentDescription = "닫기"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))

                // Timer Text
                Text(
                    text = "00:00:00",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Guide Text
                Text(
                    text = "독서를 시작해보세요",
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Play Button
                IconButton(
                    onClick = {},
                    modifier = Modifier.size(80.dp)
                ) {
                    Image(
                        painter = painterResource(id = OdokIcons.PlayButton), // drawable 파일명
                        contentDescription = "시작",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(120.dp))

                // Book Info View
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    // 책 표지 이미지
//                    Image(
//                        painter = painterResource(id = OdokIcons.Plant), // TODO: 실제 리소스로 대체
//                        contentDescription = "책 표지",
//                        modifier = Modifier
//                            .size(60.dp)
//                            .clip(RoundedCornerShape(4.dp))
//                    )
                    BookCover(
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        book = book,
                        modifier = Modifier
                            .width(80.dp)
                            .height(120.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(book.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold)
                        Text(book.author,
                            modifier = Modifier.padding(top=4.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OdokColors.StealGray )
                        Text(book.progressText,
                            modifier = Modifier.padding(top=4.dp),
                            fontSize = 14.sp,
                            color = OdokColors.StealGray)
                        Text(book.progressPercentage.toString(),
                            modifier = Modifier.padding(top=4.dp),
                            fontSize = 14.sp,
                            color = OdokColors.StealGray)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Complete Button
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OdokColors.Black
                    )

                ) {
                    Icon(Icons.Default.Check, contentDescription = "완료")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("완료")
                }
            }
        }
    }
}