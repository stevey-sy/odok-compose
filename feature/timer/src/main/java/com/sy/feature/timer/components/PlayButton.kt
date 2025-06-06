package com.sy.feature.timer.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sy.feature.timer.TimerUiState
import com.sy.feature.timer.TimerViewModel
import com.sy.odokcompose.core.designsystem.icon.OdokIcons

@Composable
fun PlayButton(
    viewModel: TimerViewModel,
    uiState: TimerUiState
) {
    IconButton(
        onClick = { viewModel.onPlayButtonClick() },
        modifier = Modifier.size(60.dp)
    ) {
        Image(
            painter = painterResource(
                id = when (uiState) {
                    TimerUiState.Reading -> OdokIcons.PauseButton
                    else -> OdokIcons.PlayButton
                }
            ),
            contentDescription = if (uiState == TimerUiState.Reading) "일시정지" else "시작",
            modifier = Modifier.fillMaxSize()
        )
    }
}