package com.sy.odokcompose.feature.mylibrary.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sy.odokcompose.feature.mylibrary.BookDetailScreen
import com.sy.odokcompose.feature.mylibrary.MyLibraryScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

const val MY_LIBRARY_ROUTE = "my_library"
const val BOOK_DETAIL_ROUTE = "book_detail"

fun NavController.navigateToMyLibrary() {
    this.navigate(MY_LIBRARY_ROUTE)
}

fun NavGraphBuilder.myLibraryScreen(
    onNavigateToSearch: () -> Unit,
    onBookItemClicked: (itemId: Int) -> Unit,
) {
    composable(route = MY_LIBRARY_ROUTE) {
        MyLibraryScreen(
            onNavigateToSearch = onNavigateToSearch,
            onBookItemClicked = onBookItemClicked)
    }
}

fun NavController.navigateToBookDetail(itemId: Int, navOptions: NavOptions? = null) {
    this.navigate("$BOOK_DETAIL_ROUTE/$itemId", navOptions)
}

fun NavGraphBuilder.bookDetailScreen(
    route: String = "$BOOK_DETAIL_ROUTE/{itemId}"
) {
    composable(
        route = route,
        arguments = listOf(
            navArgument("itemId"){
                type= NavType.IntType
            }
        )
    ) {
        BookDetailScreen()
    }
}