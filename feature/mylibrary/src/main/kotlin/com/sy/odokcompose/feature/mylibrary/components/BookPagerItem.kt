package com.sy.odokcompose.feature.mylibrary.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.sy.odokcompose.model.BookUiModel


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BookPagerItem(
    scale: Float,
    book: BookUiModel,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(8.dp, RoundedCornerShape(10.dp))
            .background(Color.White, RoundedCornerShape(10.dp))
    ) {
        SubcomposeAsyncImage(
            model = book.coverImageUrl,
            contentDescription = book.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}