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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.ui.util.lerp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.sy.odokcompose.core.designsystem.R


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClose: (page: Int, elapsedTimeSeconds: Int, isFinished: Boolean) -> Unit = { _, _, _ -> },
    viewModel: TimerViewModel = hiltViewModel()
) {
    val book by viewModel.book.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val timerText by viewModel.timerText.collectAsState()
    val guideText by viewModel.guideText.collectAsState()
    val backgroundColor by viewModel.backgroundColor.collectAsState()
    val textColor by viewModel.textColor.collectAsState()

    val isPageInputModalVisible by viewModel.isPageInputModalVisible.collectAsState()
    val lastReadPageInput by viewModel.lastReadPageInput.collectAsState()
    val sheetState = rememberModalBottomSheetState()

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

    val lpRotationAngle = remember { Animatable(0f) }
    val armAnimationProgress = remember { Animatable(0f) }
    val volumeAnimationProgress = remember { Animatable(0f) }
    val powerButtonRotation = remember { Animatable(0f) }
    val bookCoverHorizontalOffset = remember { Animatable(0f) } // 0f is center, 1f is moved left

    val shouldLpRotate = remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenWidthDpValue = configuration.screenWidthDp.toFloat() // Get screen width as Float
    // Calculate the offset needed to move BookCover (0.45f of screen width, centered) to the left edge
    val bookCoverWidthValue = screenWidthDpValue * 0.55f
    val targetBookCoverOffsetXValue = -(screenWidthDpValue / 2) + (bookCoverWidthValue / 2) // Result is Float

    LaunchedEffect(uiState) {
        if (uiState == TimerUiState.Reading) {
            shouldLpRotate.value = false
            lpRotationAngle.stop()

            // 1. BookCover animation
            bookCoverHorizontalOffset.animateTo(
                targetValue = 1f, // 1f represents the fully moved left state
                animationSpec = tween(durationMillis = 500, easing = LinearEasing)
            )

            // 2. Power button animation
            powerButtonRotation.animateTo(
                targetValue = 90f,
                animationSpec = tween(durationMillis = 300, easing = LinearEasing)
            )

            // 3. Tonearm and Volume animations in parallel
            val armJob = launch {
                armAnimationProgress.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 500, easing = LinearEasing))
            }
            val volumeJob = launch {
                volumeAnimationProgress.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 300, easing = LinearEasing))
            }
            armJob.join() // Wait for arm animation to complete
            volumeJob.join() // Wait for volume animation to complete

            // 4. Start LP rotation
            shouldLpRotate.value = true

        } else { // Not Reading
            shouldLpRotate.value = false
            lpRotationAngle.stop()

            // Animate all back to off state in parallel
            launch { bookCoverHorizontalOffset.animateTo(0f, tween(durationMillis = 500)) } // Back to center
            launch { powerButtonRotation.animateTo(0f, tween(durationMillis = 300)) }
            launch { armAnimationProgress.animateTo(0f, tween(durationMillis = 700)) }
            launch { volumeAnimationProgress.animateTo(0f, tween(durationMillis = 500)) }
        }
    }

    LaunchedEffect(shouldLpRotate.value, uiState) {
        if (shouldLpRotate.value && uiState == TimerUiState.Reading) {
            lpRotationAngle.animateTo(
                targetValue = lpRotationAngle.value + 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 4000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            lpRotationAngle.stop()
        }
    }

    // Completed 시 자동으로 화면 나가기
    LaunchedEffect(uiState) {
        if (uiState == TimerUiState.Completed) {
            onClose(viewModel.getLastReadPageInt(),
                viewModel.getElapsedTimeSeconds(),
                viewModel.isBookReadingCompleted()
                )
        }
    }

    OdokTheme {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            containerColor = Color(backgroundColor)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    IconButton(onClick = { onClose(-1, -1, false) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            modifier = Modifier.size(80.dp),
                            contentDescription = "닫기",
                            tint = Color(textColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

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

                        rotate(lpRotationAngle.value, center) {
                            drawCircle(
                                color = Color(0xFF353935),
                                radius = canvasHeight * 0.38f,
                                center = center
                            )
                            drawCircle(
                                color = Color.Black,
                                radius = canvasHeight * 0.18f,
                                center = center
                            )
                            drawCircle(
                                color = Color(0xFFE53935),
                                radius = canvasHeight * 0.11f,
                                center = center
                            )
                            drawCircle(
                                color = Color.Black,
                                radius = canvasHeight * 0.02f,
                                center = center
                            )
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
                        val armStartOff = Offset(canvasWidth * 0.85f, canvasHeight * 0.85f)
                        val armStartOn = Offset(center.x + canvasHeight * 0.23f, center.y + canvasHeight * 0.1f)
                        val armEnd = Offset(canvasWidth * 0.85f, canvasHeight * 0.18f)

                        val interpolatedArmStart = androidx.compose.ui.geometry.lerp(armStartOff, armStartOn, armAnimationProgress.value)

                        drawCircle(
                            color = OdokColors.Orange,
                            radius = 40f,
                            center = armEnd
                        )
                        drawLine(
                            color = Color.Black,
                            start = interpolatedArmStart,
                            end = armEnd,
                            strokeWidth = 12f
                        )

                        drawRect(
                            color = OdokColors.Orange,
                            topLeft = Offset(interpolatedArmStart.x - 12f, interpolatedArmStart.y - 12f),
                            size = androidx.compose.ui.geometry.Size(24f, 24f)
                        )

                        val ctrlX = canvasWidth * 0.92f
                        val volumeOnTopLeft = Offset(ctrlX - 18f, canvasHeight * 0.45f)
                        val volumeOffTopLeft = Offset(ctrlX - 18f, canvasHeight * 0.65f)

                        val interpolatedVolumeTopLeft = androidx.compose.ui.geometry.lerp(volumeOffTopLeft, volumeOnTopLeft, volumeAnimationProgress.value)

                        drawLine(
                            color = Color.Black,
                            start = Offset(ctrlX, canvasHeight * 0.4f),
                            end = Offset(ctrlX, canvasHeight * 0.7f),
                            strokeWidth = 12f
                        )
                        drawRect(
                            color = OdokColors.Orange,
                            topLeft = interpolatedVolumeTopLeft,
                            size = androidx.compose.ui.geometry.Size(36f, 18f)
                        )

                        val powerX = canvasWidth * 0.80f
                        val powerButtonCenter = Offset(powerX, canvasHeight * 0.8f)

                        drawCircle(
                            color = OdokColors.Orange,
                            radius = 20f,
                            center = powerButtonCenter
                        )
                        rotate(degrees = powerButtonRotation.value, pivot = powerButtonCenter) {
                            drawLine(
                                color = Color.Black,
                                start = Offset(powerButtonCenter.x - 14f, powerButtonCenter.y),
                                end = Offset(powerButtonCenter.x + 14f, powerButtonCenter.y),
                                strokeWidth = 4f
                            )
                        }
                    }
//                    BookCover(
//                        sharedTransitionScope = sharedTransitionScope,
//                        animatedVisibilityScope = animatedVisibilityScope,
//                        book = book,
//                        modifier = Modifier
//                            .fillMaxWidth(0.42f)
//                            .fillMaxHeight()
//                            .offset(x = lerp(0f, targetBookCoverOffsetXValue, bookCoverHorizontalOffset.value).dp)
//                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
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

                    Column (
                        modifier = Modifier
                            .fillMaxHeight() // 중요
                    ) {
                        Text(book.getTitleText(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(textColor),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis)
                        Text(book.author,
                            modifier = Modifier.padding(top=4.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OdokColors.StealGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis)
                        Text(book.progressText,
                            modifier = Modifier.padding(top=4.dp),
                            fontSize = 14.sp,
                            color = Color(textColor))
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = timerText,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(textColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = guideText,
                    fontSize = 16.sp,
                    color = Color(textColor)
                )

                Spacer(modifier = Modifier.height(20.dp))

                IconButton(
                    onClick = { viewModel.onPlayButtonClick() },
                    modifier = Modifier.size(60.dp)
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

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {  },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OdokColors.Orange
                    ),
                ) {
                    Image(
                        painter = painterResource(id = OdokIcons.Write),
                        contentDescription = "메모하기",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("메모하기")
                }

                Button(
                    onClick = { viewModel.onCompleteClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OdokColors.Black
                    ),
                ) {
                    Icon(Icons.Default.Check, contentDescription = "완료")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("완료")
                }
            }

            // Modal Bottom Sheet for Page Input
            if (isPageInputModalVisible) {
                LaunchedEffect(Unit) {
                    // 모달이 처음 열릴 때만 초기값 설정
                    viewModel.onLastReadPageInputChange(book.currentPageCnt.toString())
                }
                ModalBottomSheet(
                    onDismissRequest = { viewModel.dismissPageInputModal() },
                    sheetState = sheetState
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("마지막으로 읽은 페이지를 입력하세요")
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("p.",)
                            TextField(
                                value = lastReadPageInput,
                                onValueChange = { viewModel.onLastReadPageInputChange(it) },
                                modifier = Modifier.width(100.dp), // 입력칸 너비 조절
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = TextStyle(textAlign = TextAlign.Center)
                                // label = { Text("페이지") } // 라벨은 Row 바깥으로 빼거나 다른 형태로 표시
                            )
                            Text(" / ${book.totalPageCnt}",)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.saveLastReadPageAndDismiss() },
                            enabled = lastReadPageInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OdokColors.Black, // 원하는 배경색
                                contentColor = Color.White          // 텍스트 색
                            )
                        ) {
                            Text("저장")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}