package com.sy.odokcompose.feature.mylibrary.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sy.odokcompose.core.designsystem.OdokColors
import com.sy.odokcompose.core.designsystem.icon.OdokIcons

@Composable
fun CommentSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Comments 0",
                textAlign = TextAlign.Center,
                color = OdokColors.StealGray,
                fontWeight = FontWeight.Light,
                fontSize = 18.sp,
                maxLines = 1,
            )
            Image(
                painter = painterResource(id = OdokIcons.DoubleDownArrow),
                contentDescription = "down arrow",
                modifier = Modifier.size(24.dp),
            )
        }
    }
}