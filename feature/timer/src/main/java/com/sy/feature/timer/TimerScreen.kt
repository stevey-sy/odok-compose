package com.sy.feature.timer

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sy.odokcompose.core.designsystem.OdokTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.airbnb.lottie.compose.LottieAnimation
import com.sy.feature.timer.components.BookInfo
import com.sy.feature.timer.components.CloseButton
import com.sy.feature.timer.components.CompleteButton
import com.sy.feature.timer.components.LpPlayerView
import com.sy.feature.timer.components.MemoButton
import com.sy.feature.timer.components.PageInputModal
import com.sy.feature.timer.components.PlayButton
import com.sy.feature.timer.components.SummaryModal
import com.sy.odokcompose.core.designsystem.OdokColors

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onMemoClick: (itemId: Int) -> Unit,
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
    val isSummaryModalVisible by viewModel.isSummaryModalVisible.collectAsState()
    val isMemoSelectModalVisible by viewModel.isMemoSelectModalVisible.collectAsState()

    val lastReadPageInput by viewModel.lastReadPageInput.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val summarySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val memoSelectSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val lpRotationAngle = remember { Animatable(0f) }
    val armAnimationProgress = remember { Animatable(0f) }
    val volumeAnimationProgress = remember { Animatable(0f) }
    val powerButtonRotation = remember { Animatable(0f) }
    val bookCoverHorizontalOffset = remember { Animatable(0f) } // 0f is center, 1f is moved left

    val shouldLpRotate = remember { mutableStateOf(false) }

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

        if (uiState == TimerUiState.Completed) {
            onClose(viewModel.getLastReadPageInt(),
                viewModel.getElapsedTimeSeconds(),
                viewModel.isBookReadingCompleted()
            )
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
                CloseButton(onClose)

                Spacer(modifier = Modifier.height(30.dp))

                LpPlayerView(
                    lpRotationAngle,
                    armAnimationProgress,
                    volumeAnimationProgress,
                    powerButtonRotation
                )

                Spacer(modifier = Modifier.height(10.dp))

                BookInfo(
                    sharedTransitionScope,
                    animatedVisibilityScope,
                    book,
                    textColor,
                    timerText
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = guideText,
                    fontSize = 16.sp,
                    color = Color(textColor)
                )

                Spacer(modifier = Modifier.height(20.dp))

                PlayButton(viewModel, uiState)

                Spacer(modifier = Modifier.weight(1f))

                MemoButton(viewModel)

                CompleteButton(viewModel)
            }

            // Modal Bottom Sheet for Page Input
            PageInputModal(isPageInputModalVisible, viewModel, book, sheetState, lastReadPageInput)

            SummaryModal(isSummaryModalVisible, viewModel, summarySheetState, timerText)

            if(isMemoSelectModalVisible) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.dismissMemoSelectModal() },
                    sheetState = memoSelectSheetState
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
                            Text("음성인식으로 가져오기")
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
                            Text("사진으로 가져오기")
                        }
                       
                        Button(
                            onClick = {
                                viewModel.dismissMemoSelectModal()
                                onMemoClick(book.itemId) },
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
                            Text("직접 입력하기")
                        }
                    }
                }
            }
        }
    }
}