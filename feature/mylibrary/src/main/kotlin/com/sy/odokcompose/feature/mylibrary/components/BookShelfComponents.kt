package com.sy.odokcompose.feature.mylibrary.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.sy.odokcompose.core.designsystem.OdokColors


@Composable
fun BookShelfBase(
    modifier: Modifier = Modifier,
    colorTopLine: Color = OdokColors.BookShelf,
    colorShelf: Color = OdokColors.BookShelfCenter,
    colorShadow: Color = OdokColors.BookShelfShadow
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // 선반 상단 경계선
        drawRect(
            color = colorTopLine,
            topLeft = Offset(0f, 0f),
            size = Size(width, height * 0.3f)
        )

        // 선반 본체 (중앙)
        drawRect(
            color = colorShelf,
            topLeft = Offset(0f, height * 0.3f),
            size = Size(width, height * 0.25f)
        )

        // 그림자 효과 (선반 아래)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(colorShadow, Color.White),
                startY = height * 0.55f,
                endY = height
            ),
            topLeft = Offset(0f, height * 0.55f),
            size = Size(width, height * 0.45f)
        )
    }
}

@Composable
fun BookShelfLeft(
    modifier: Modifier = Modifier,
    colorTopLine: Color = OdokColors.BookShelf,
    colorShelf: Color = OdokColors.BookShelfCenter,
    colorShadow: Color = OdokColors.BookShelfShadow
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // 선반 상단 경계선
        drawRect(
            color = colorTopLine,
            topLeft = Offset(0f, 0f),
            size = Size(width, height * 0.3f)
        )

        // 역삼각형 (왼쪽)
        drawPath(
            path = Path().apply {
                moveTo(0f, 0f)
                lineTo(width * 0.05f, 0f)
                lineTo(0f, height * 0.2f)
                close()
            },
            color = Color.White
        )

        // 선반 본체 (중앙)
        drawRect(
            color = colorShelf,
            topLeft = Offset(0f, height * 0.3f),
            size = Size(width, height * 0.25f)
        )

        // 그림자 효과 (선반 아래)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(colorShadow, Color.White),
                startY = height * 0.55f,
                endY = height
            ),
            topLeft = Offset(0f, height * 0.55f),
            size = Size(width, height * 0.45f)
        )
    }
}

@Composable
fun BookShelfRight(
    modifier: Modifier = Modifier,
    colorTopLine: Color = OdokColors.BookShelf,
    colorShelf: Color = OdokColors.BookShelfCenter,
    colorShadow: Color = OdokColors.BookShelfShadow
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // 선반 상단 경계선
        drawRect(
            color = colorTopLine,
            topLeft = Offset(0f, 0f),
            size = Size(width, height * 0.3f)
        )

        // 역삼각형 (오른쪽)
        drawPath(
            path = Path().apply {
                moveTo(width, 0f)
                lineTo(width * 0.95f, 0f)
                lineTo(width, height * 0.2f)
                close()
            },
            color = Color.White
        )

        // 선반 본체 (중앙)
        drawRect(
            color = colorShelf,
            topLeft = Offset(0f, height * 0.3f),
            size = Size(width, height * 0.25f)
        )

        // 그림자 효과 (선반 아래)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(colorShadow, Color.White),
                startY = height * 0.55f,
                endY = height
            ),
            topLeft = Offset(0f, height * 0.55f),
            size = Size(width, height * 0.45f)
        )
    }
} 