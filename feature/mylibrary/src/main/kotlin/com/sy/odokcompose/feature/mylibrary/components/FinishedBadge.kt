package com.sy.odokcompose.feature.mylibrary.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sy.odokcompose.core.designsystem.OdokColors
import com.sy.odokcompose.core.designsystem.icon.OdokIcons
import com.sy.odokcompose.model.BookUiModel
import kotlinx.coroutines.delay

@Composable
fun FinishedBadge(
    book: BookUiModel,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )
    
    val offsetY by animateOffsetAsState(
        targetValue = if (isVisible) Offset(-60f, -100f) else Offset(-60f, 0f),
        animationSpec = tween(durationMillis = 1000),
        label = "offset"
    )

    LaunchedEffect(book.finishedReadCnt) {
        if (book.finishedReadCnt > 0) {
            delay(100)
            isVisible = true
        } else {
            isVisible = false
        }
    }

    Box(
        modifier = modifier
            .size(55.dp)
            .offset { IntOffset(offsetY.x.toInt(), offsetY.y.toInt()) }
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = OdokIcons.Wreath),
            contentDescription = "완독수",
            modifier = Modifier.fillMaxSize()
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "완독!",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = OdokColors.Black,
            )
            Text(
                text = "${book.finishedReadCnt}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = OdokColors.Black
            )
        }
    }
} 