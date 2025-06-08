package com.sy.odokcompose.feature.mylibrary.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SimpleSpeechBubble(
    text: String,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val boxWidth = LocalConfiguration.current.screenWidthDp.dp - 80.dp // 40.dp padding on each side

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 700),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .offset(x = (boxWidth * animatedProgress + 10.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier.width(IntrinsicSize.Min)
            modifier = Modifier.width(56.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Black, RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = text,
                    fontSize = 11.sp,
                    color = Color.White)
            }
            // 꼬리 부분
            Canvas(
                modifier = Modifier
                    .size(10.dp, 6.dp)
            ) {
                drawPath(
                    path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width / 2, size.height)
                        close()
                    },
                    color = Color.Black
                )
            }
        }
    }
}