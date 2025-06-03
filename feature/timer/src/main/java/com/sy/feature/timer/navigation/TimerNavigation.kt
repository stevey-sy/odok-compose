package com.sy.feature.timer.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sy.feature.timer.TimerScreen

const val TIMER = "timer"

fun NavController.navigateToTimer(itemId: Int,
                                  navOptions: NavOptions? = null) {
    this.navigate("$TIMER/$itemId", navOptions)
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.timerScreen(
    sharedTransitionScope: SharedTransitionScope,
    onCloseClicked: () -> Unit,
    route: String = "$TIMER/{itemId}"
) {
    composable(route = route,
        arguments = listOf(
            navArgument("itemId"){
                type = NavType.IntType
            }
        )
    ) {
        TimerScreen(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this,
            onClose = onCloseClicked
        )
    }

}