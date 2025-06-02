package com.sy.odokcompose.feature.mylibrary.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sy.odokcompose.core.designsystem.icon.OdokIcons

@Composable
fun BookActionButtons() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(44.dp)
    ) {
        // 독서 버튼
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    color = Color.Black,
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        bottomStart = 20.dp
                    )
                )
                .clickable { /* TODO: 독서 클릭 */ },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = OdokIcons.StopWatch),
                    contentDescription = "독서",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("독서", color = Color.White)
            }
        }

        // 경계선 (흰색)
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(Color.White)
        )

        // 기록 버튼
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    color = Color.Black,
                    shape = RoundedCornerShape(
                        topEnd = 20.dp,
                        bottomEnd = 20.dp
                    )
                )
                .clickable { /* TODO: 기록 클릭 */ },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = OdokIcons.Write),
                    contentDescription = "메모",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("메모", color = Color.White)
            }
        }
    }
} 