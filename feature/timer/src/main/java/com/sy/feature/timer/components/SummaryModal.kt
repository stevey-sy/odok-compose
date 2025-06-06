package com.sy.feature.timer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.sy.feature.timer.TimerViewModel
import com.sy.odokcompose.core.designsystem.OdokColors
import com.sy.odokcompose.core.designsystem.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SummaryModal(
    isSummaryModalVisible: Boolean,
    viewModel: TimerViewModel,
    summarySheetState: SheetState,
    timerText: String
) {
    if (isSummaryModalVisible) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.anim_complete))
        val clipSpec = LottieClipSpec.Progress(0f, 0.7f)
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = 1,
            clipSpec = clipSpec
        )

        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissSummaryModal() },
            sheetState = summarySheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(150.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 읽은 페이지 수
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "읽은 페이지",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = OdokColors.StealGray
                        )
                    )
                    Text(
                        text = "${viewModel.getReadPageInt()} 페이지",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 읽은 시간
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "읽은 시간",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = OdokColors.StealGray
                        )
                    )
                    Text(
                        text = timerText,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 총 누적 읽은 시간
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "총 누적 읽은 시간",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = OdokColors.StealGray
                        )
                    )
                    Text(
                        text = viewModel.getTotalTime(),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 확인 버튼
                Button(
                    onClick = { viewModel.dismissSummaryModal() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OdokColors.Black
                    )
                ) {
                    Text(
                        text = "확인",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}