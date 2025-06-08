package com.sy.feature.memo.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sy.feature.memo.AddMemoScreen

const val ADD_MEMO = "add_memo"
const val EDIT_MEMO_ROUTE = "edit_memo?itemId={itemId}&memoId={memoId}"

fun NavController.navigateToAddMemo(itemId: Int,navOptions: NavOptions? = null) {
    this.navigate("$ADD_MEMO/$itemId", navOptions)
}

fun NavController.navigateToEditMemo(
    itemId: Int,
    memoId: Int,
    navOptions: NavOptions? = null
) {
    // 기본 route + itemId
    val base = "edit_memo?itemId=$itemId"
    // memoId가 있을 때만 쿼리 추가
    val route = memoId.let { "$base&memoId=$it" }
    this.navigate(route, navOptions)
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

fun NavGraphBuilder.editMemoScreen(
    onClose: () -> Unit,
    route: String = EDIT_MEMO_ROUTE
) {
    composable (
        route = route,
        arguments = listOf(
            navArgument("itemId") {
                type = NavType.IntType
            },
            navArgument("memoId") {
                type = NavType.IntType
                defaultValue = -1
            }
        )
    ) {
        AddMemoScreen(
            onClose = onClose
        )
    }
}