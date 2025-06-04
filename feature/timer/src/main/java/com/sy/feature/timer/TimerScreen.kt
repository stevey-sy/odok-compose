package com.sy.feature.timer

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColor
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.geometry.lerp

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

    val rotation = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    val armAnimationProgress by animateFloatAsState(
        targetValue = if (uiState == TimerUiState.Reading) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
        label = "armProgress"
    )

    LaunchedEffect(uiState) {
        if (uiState == TimerUiState.Reading) {
            coroutineScope.launch {
                rotation.animateTo(
                    targetValue = rotation.value + 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 4000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
            }
        } else {
            rotation.snapTo(0f)
        }
    }

    OdokTheme {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
//            containerColor = if (uiState == TimerUiState.Reading) animatedColor else Color(backgroundColor)
            containerColor = Color(backgroundColor)
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

                // Orange Circle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 16.dp)
                    ) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        val center = Offset(canvasWidth / 2, canvasHeight / 2)

                        // 1. LP판(큰 검은색 원)만 회전
                        rotate(rotation.value, center) {
                            drawCircle(
                                color = Color(0xFF23272A),
                                radius = canvasHeight * 0.38f,
                                center = center
                            )
                            // 2. 중앙의 검은색 원
                            drawCircle(
                                color = Color.Black,
                                radius = canvasHeight * 0.18f,
                                center = center
                            )
                            // 3. 중앙의 빨간색 원
                            drawCircle(
                                color = Color(0xFFE53935),
                                radius = canvasHeight * 0.11f,
                                center = center
                            )
                            // 4. 회전 느낌의 곡선(레코드판 위)
                            drawArc(
                                color = Color(0xFF90A4AE),
                                startAngle = 200f,
                                sweepAngle = 40f,
                                useCenter = false,
                                topLeft = Offset(center.x - canvasHeight * 0.32f, center.y - canvasHeight * 0.32f),
                                size = androidx.compose.ui.geometry.Size(canvasHeight * 0.64f, canvasHeight * 0.64f),
                                style = Stroke(width = 6f)
                            )
                            drawArc(
                                color = Color(0xFF90A4AE),
                                startAngle = 30f,
                                sweepAngle = 30f,
                                useCenter = false,
                                topLeft = Offset(center.x - canvasHeight * 0.22f, center.y - canvasHeight * 0.22f),
                                size = androidx.compose.ui.geometry.Size(canvasHeight * 0.44f, canvasHeight * 0.44f),
                                style = Stroke(width = 4f)
                            )
                        }
                        // 6. 톤암과 레코드판의 연결부(작은 오렌지 사각형)
                        val armStartOff = Offset(canvasWidth * 0.85f, canvasHeight * 0.88f)
                        val armStartOn = Offset(center.x + canvasHeight * 0.23f, center.y + canvasHeight * 0.1f)
                        val armEnd = Offset(canvasWidth * 0.85f, canvasHeight * 0.18f)

                        val interpolatedArmStart = lerp(armStartOff, armStartOn, armAnimationProgress)

                        // 7. 톤암 끝의 오렌지 원
                        drawCircle(
                            color = Color(0xFFFFB74D),
                            radius = 40f,
                            center = armEnd
                        )
                        // 5. 톤암(검은색 선) - 오렌지 원 위로 그리기
                        drawLine(
                            color = Color.Black,
                            start = interpolatedArmStart,
                            end = armEnd,
                            strokeWidth = 12f
                        )
                        drawRect(
                            color = Color(0xFFFFB74D),
                            topLeft = Offset(interpolatedArmStart.x - 12f, interpolatedArmStart.y - 12f),
                            size = androidx.compose.ui.geometry.Size(24f, 24f)
                        )
                        // 8. 오른쪽 컨트롤러(세로 막대 + 사각형)
                        val ctrlX = canvasWidth * 0.92f
                        drawLine(
                            color = Color.Black,
                            start = Offset(ctrlX, canvasHeight * 0.25f),
                            end = Offset(ctrlX, canvasHeight * 0.7f),
                            strokeWidth = 8f
                        )
                        drawRect(
                            color = Color(0xFFFFB74D),
                            topLeft = Offset(ctrlX - 18f, canvasHeight * 0.32f),
                            size = androidx.compose.ui.geometry.Size(36f, 18f)
                        )
                        // 9. 작은 오렌지 원(버튼)
                        drawCircle(
                            color = Color(0xFFFFB74D),
                            radius = 14f,
                            center = Offset(ctrlX - 40f, canvasHeight * 0.8f)
                        )
                        // 10. 작은 검은색 선(버튼 위)
                        drawLine(
                            color = Color.Black,
                            start = Offset(ctrlX - 48f, canvasHeight * 0.8f),
                            end = Offset(ctrlX - 32f, canvasHeight * 0.8f),
                            strokeWidth = 4f
                        )
                    }
                }

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