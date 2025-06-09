package com.sy.odokcompose.feature.mylibrary.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sy.odokcompose.core.designsystem.OdokColors

@Composable
fun BookProgress(
    progressPercentage: Int,
    progressText: String
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            SimpleSpeechBubble(
                text = "${progressPercentage}%",
                progress = progressPercentage * 0.01f
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .padding(horizontal = 40.dp)
            ) {
                // 배경 (회색)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .align(Alignment.BottomStart)
                )

                val animatedProgress by animateFloatAsState(
                    targetValue = progressPercentage * 0.01f,
                    animationSpec = tween(durationMillis = 700),
                    label = "progressBar"
                )

                // 진행도 (검정색)
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .fillMaxWidth(animatedProgress)
                        .background(
//                            color = if (progressPercentage == 0) Color.Transparent else Color.Black,
                            color = Color.Black,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .align(Alignment.BottomStart)
                )
            }


            Text(
                text = progressText,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .fillMaxWidth(),

                textAlign = TextAlign.Center,
                color = OdokColors.NormalGray,
                fontWeight = FontWeight.Light,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
} 