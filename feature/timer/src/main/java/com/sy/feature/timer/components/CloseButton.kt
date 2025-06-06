package com.sy.feature.timer.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sy.odokcompose.core.designsystem.icon.OdokIcons

@Composable
fun CloseButton(
    onClose: (Int, Int, Boolean) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopEnd
    ) {
        IconButton(onClick = { onClose(-1, -1, false) }) {
            Image(
                painter = painterResource(id = OdokIcons.CloseButton),
                contentDescription = "닫기",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}