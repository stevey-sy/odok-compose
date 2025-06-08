package com.sy.odokcompose.feature.mylibrary.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.sy.odokcompose.model.BookUiModel

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
fun PagerItem(
    page: Int,
    pagerState: PagerState,
    sharedTransitionScope: SharedTransitionScope,
    book: BookUiModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    scale: Float
) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val itemWidth = screenWidth * 0.7f // 디바이스 너비의 60%


    if (page == pagerState.currentPage) {
        // 선택된 아이템 (sharedElement 적용)
        with(sharedTransitionScope) {
            Box(
                modifier = Modifier
                    .width(itemWidth)
                    .aspectRatio(0.7f) // 책 표지 비율 기준
//                    .height(280.dp)
                    .padding(20.dp)
                    .sharedElement(
                        rememberSharedContentState("bookItem/${book.itemId}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { initial, target -> tween(700) }
                    )
            ) {
                BookPagerItem(1f, book)
                if (book.finishedReadCnt > 0) {
                    FinishedBadge(book = book)
                }
            }
        }
    } else {
        // 비선택 아이템 (sharedElement 없이)
        Box(
            modifier = Modifier
                .width(itemWidth)
                .aspectRatio(0.7f)
//                .width(200.dp)
//                .height(280.dp)
                .padding(20.dp)
        ) {
            BookPagerItem(scale, book)
        }
    }
}