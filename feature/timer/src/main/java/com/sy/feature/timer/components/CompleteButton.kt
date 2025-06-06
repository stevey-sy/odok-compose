package com.sy.feature.timer.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sy.feature.timer.TimerViewModel
import com.sy.odokcompose.core.designsystem.OdokColors

@Composable
fun CompleteButton(viewModel: TimerViewModel) {
    Button(
        onClick = { viewModel.onCompleteClick() },
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .padding(bottom = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = OdokColors.Black
        ),
    ) {
        Icon(Icons.Default.Check, contentDescription = "완료")
        Spacer(modifier = Modifier.width(8.dp))
        Text("완료")
    }
}