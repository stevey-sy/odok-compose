package com.sy.feature.timer.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sy.odokcompose.core.designsystem.OdokColors
import com.sy.core.ui.components.BookCover
import com.sy.odokcompose.model.BookUiModel

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
fun BookInfo(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    book: BookUiModel,
    textColor: Int,
    timerText: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp)
    ) {
        BookCover(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope,
            book = book,
            modifier = Modifier
                .width(70.dp)
                .height(100.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .wrapContentHeight()
        ) {
            Text(
                book.getTitleText(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(textColor),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                book.author,
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = OdokColors.StealGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                book.progressText,
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 14.sp,
                color = Color(textColor)
            )

            Text(
                text = timerText,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Color(textColor)
            )
        }
    }
}