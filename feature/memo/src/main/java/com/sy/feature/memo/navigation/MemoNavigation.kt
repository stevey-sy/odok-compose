package com.sy.feature.memo.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sy.feature.memo.AddMemoScreen

const val ADD_MEMO = "add_memo"

fun NavController.navigateToAddMemo(itemId: Int,navOptions: NavOptions? = null) {
    this.navigate("$ADD_MEMO/$itemId", navOptions)
}

fun NavGraphBuilder.addMemoScreen(
    onClose: () -> Unit,
    route: String = "$ADD_MEMO/{itemId}"
) {
    composable (
        route = route,
        arguments = listOf(
            navArgument("itemId") {
                type = NavType.IntType
            }
        )
    ) {
        AddMemoScreen(
            onClose = onClose
        )
    }
}