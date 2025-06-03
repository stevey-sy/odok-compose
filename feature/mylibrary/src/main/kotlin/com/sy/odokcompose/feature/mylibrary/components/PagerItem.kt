package com.sy.odokcompose.feature.mylibrary.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import com.sy.odokcompose.core.designsystem.OdokColors
import com.sy.odokcompose.model.BookUiModel
import com.sy.odokcompose.core.designsystem.icon.OdokIcons

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
    if (page == pagerState.currentPage) {
        // 선택된 아이템 (sharedElement 적용)
        with(sharedTransitionScope) {
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(280.dp)
                    .padding(20.dp)
                    .sharedElement(
                        rememberSharedContentState("bookItem/${book.itemId}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { initial, target -> tween(700) }
                    )
            ) {
                BookPagerItem(1f, book)
                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .offset { IntOffset(-60, -100) },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = OdokIcons.Wreath),
                        contentDescription = "식물",
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
                            text = "${page + 1}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = OdokColors.Black
                        )
                    }
                }
            }
        }
    } else {
        // 비선택 아이템 (sharedElement 없이)
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(280.dp)
                .padding(20.dp)
        ) {
            BookPagerItem(scale, book)
        }
    }
}