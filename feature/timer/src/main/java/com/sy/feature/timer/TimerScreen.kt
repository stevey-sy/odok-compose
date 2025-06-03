package com.sy.feature.timer

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.core.*

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TimerScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClose: () -> Unit = {},
    viewModel: TimerViewModel = hiltViewModel()
) {
    val book by viewModel.book.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val timerText by viewModel.timerText.collectAsState()
    val guideText by viewModel.guideText.collectAsState()
    val backgroundColor by viewModel.backgroundColor.collectAsState()
    val textColor by viewModel.textColor.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    val animatedColor by infiniteTransition.animateColor(
        initialValue = Color(0xFF4A148C),
        targetValue = Color(0xFF4169E1), // 밝은 블루
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color"
    )

    OdokTheme {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            containerColor = if (uiState == TimerUiState.Reading) animatedColor else Color(backgroundColor)
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
                            contentDescription = "닫기",
                            tint = Color(textColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))

                // Timer Text
                Text(
                    text = timerText,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(textColor)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Guide Text
                Text(
                    text = guideText,
                    fontSize = 16.sp,
                    color = Color(textColor)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Play Button
                IconButton(
                    onClick = { viewModel.onPlayButtonClick() },
                    modifier = Modifier.size(80.dp)
                ) {
                    Image(
                        painter = painterResource(
                            id = when (uiState) {
                                TimerUiState.Reading -> OdokIcons.PauseButton
                                else -> OdokIcons.PlayButton
                            }
                        ),
                        contentDescription = if (uiState == TimerUiState.Reading) "일시정지" else "시작",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(120.dp))

                // Book Info View
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    BookCover(
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        book = book,
                        modifier = Modifier
                            .width(70.dp)
                            .height(100.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(book.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(textColor))
                        Text(book.author,
                            modifier = Modifier.padding(top=4.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(textColor))
                        Text(book.progressText,
                            modifier = Modifier.padding(top=4.dp),
                            fontSize = 14.sp,
                            color = Color(textColor))
                        Text(book.getElapsedTimeFormatted(),
                            modifier = Modifier.padding(top=4.dp),
                            fontSize = 14.sp,
                            color = Color(textColor))
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Memo Button
                Button(
                    onClick = {  },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OdokColors.IrisBlue
                    ),
                    enabled = uiState != TimerUiState.Completed
                ) {
                    Image(
                        painter = painterResource(id = OdokIcons.Write),
                        contentDescription = "메모하기",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("메모하기")
                }

                // Complete Button
                Button(
                    onClick = { viewModel.onCompleteClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OdokColors.Black
                    ),
                    enabled = uiState != TimerUiState.Completed
                ) {
                    Icon(Icons.Default.Check, contentDescription = "완료")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("완료")
                }
            }
        }
    }
}