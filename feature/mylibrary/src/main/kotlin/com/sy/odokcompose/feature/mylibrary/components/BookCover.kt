package com.sy.odokcompose.feature.mylibrary.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.sy.odokcompose.model.BookUiModel


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BookCover(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    book: BookUiModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 25.dp)
            .shadow(
                elevation = 8.dp,
                shape = RectangleShape
            )
            .background(Color.White)
    ) {
        with(sharedTransitionScope) {
            SubcomposeAsyncImage(
                model = book.coverImageUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxSize()
                    .sharedElement(
                        rememberSharedContentState(key = "bookItem/${book.itemId}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = {initial, taget -> tween(durationMillis = 700)}
                    ),
                contentScale = ContentScale.Crop
            )
        }
    }
} 