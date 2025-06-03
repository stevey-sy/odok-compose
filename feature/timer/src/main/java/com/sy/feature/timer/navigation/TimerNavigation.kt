package com.sy.feature.timer.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sy.feature.timer.TimerScreen

const val TIMER = "timer"

fun NavController.navigateToTimer() {
    this.navigate(TIMER)
}

fun NavGraphBuilder.timerScreen(
    onCloseClicked: () -> Unit
) {
    composable(route = TIMER) {
        TimerScreen(
            onClose = onCloseClicked
        )
    }

}