package com.sy.odokcompose.core.designsystem.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sy.odokcompose.core.designsystem.DashiFont

/**
 * 오독오독 앱에서 사용하는 커스텀 TopAppBar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OdokTopAppBar(
    title: String = "",
    fontSize: TextUnit = 36.sp,
     fontFamily: FontFamily = DashiFont,
//    fontFamily: FontFamily = FontFamily.Default,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
//        modifier = Modifier.height(56.dp), // 원하는 높이
        title = { 
            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    fontFamily = fontFamily,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Normal
                ) 
            }
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "뒤로 가기"
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        windowInsets = WindowInsets.statusBars
    )
}

/**
 * 액션 버튼을 포함한 아이콘 버튼 컴포넌트
 */
@Composable
fun ActionIconButton(
    onClick: () -> Unit,
    drawableResId: Int? = null,
    imageVector: ImageVector? = null,
    contentDescription: String
) {
    IconButton(onClick = onClick) {
        // drawable 리소스가 제공된 경우
        if (drawableResId != null) {
            Icon(
                painter = painterResource(id = drawableResId),
                contentDescription = contentDescription
            )
        } 
        // 이미지 벡터가 제공된 경우
        else if (imageVector != null) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription
            )
        }
    }
}

