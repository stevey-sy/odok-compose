package com.sy.odokcompose.feature.mylibrary.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.sy.odokcompose.model.BookUiModel
import kotlin.math.absoluteValue


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BookPager(
    bookList: List<BookUiModel>,
    currentPage: Int,
    onPageChanged: (Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val pagerState = if (bookList.isNotEmpty()) {
        rememberPagerState(
            initialPage = currentPage,
            pageCount = { bookList.size }
        )
    } else {
        rememberPagerState(
            initialPage = 0,
            pageCount = { 0 }
        )
    }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != currentPage) {
            onPageChanged(pagerState.currentPage)
        }
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val itemWidth = 200.dp
    val horizontalPadding = (screenWidth - itemWidth) / 2

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        contentPadding = PaddingValues(horizontal = horizontalPadding),
    ) { page ->
        val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
        val scale = 1f - (pageOffset.absoluteValue * 0.2f).coerceIn(0f, 0.2f)

        val book = bookList.getOrNull(page)
        if (book != null) {
            PagerItem(
                page = page,
                pagerState = pagerState,
                sharedTransitionScope = sharedTransitionScope,
                book = book,
                animatedVisibilityScope = animatedVisibilityScope,
                scale = scale
            )
        }
    }
}