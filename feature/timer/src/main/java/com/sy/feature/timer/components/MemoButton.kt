package com.sy.feature.timer.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sy.feature.timer.TimerViewModel
import com.sy.odokcompose.core.designsystem.OdokColors
import com.sy.odokcompose.core.designsystem.icon.OdokIcons

@Composable
fun MemoButton(viewModel: TimerViewModel) {
    Button(
        onClick = { viewModel.onMemoButtonClick()},
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .padding(bottom = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = OdokColors.Orange
        ),
    ) {
        Image(
            painter = painterResource(id = OdokIcons.Write),
            contentDescription = "메모하기",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("메모하기")
    }
}