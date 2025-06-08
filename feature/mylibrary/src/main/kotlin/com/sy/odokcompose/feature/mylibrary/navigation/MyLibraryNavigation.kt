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
import com.sy.odokcompose.model.type.ShelfFilterType
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

const val MY_LIBRARY_ROUTE = "my_library"
const val BOOK_DETAIL_ROUTE = "book_detail"

fun NavController.navigateToMyLibrary() {
    this.navigate(MY_LIBRARY_ROUTE)
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.myLibraryScreen(
    sharedTransitionScope: SharedTransitionScope,
    onNavigateToSearch: () -> Unit,
    onBookItemClicked: (itemId: Int, filterType:Int, searchQuery:String) -> Unit,
) {
    composable(route = MY_LIBRARY_ROUTE) {
        MyLibraryScreen(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this,
            onNavigateToSearch = onNavigateToSearch,
            onBookItemClicked = onBookItemClicked)
    }
}

fun NavController.navigateToBookDetail(itemId: Int,
                                       filterType: Int = ShelfFilterType.NONE.code,
                                       searchQuery: String = "",
                                       navOptions: NavOptions? = null) {
    this.navigate("$BOOK_DETAIL_ROUTE/$itemId/$filterType/$searchQuery", navOptions)
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.bookDetailScreen(
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    onMemoAddBtnClicked: (bookId: Int) -> Unit,
    onMemoEditBtnClicked: (bookId:Int, memoId: Int) -> Unit,
    onReadBtnClicked: (itemId: Int) -> Unit,
    route: String = "$BOOK_DETAIL_ROUTE/{itemId}/{filterType}/{searchQuery}"
) {
    composable(
        route = route,
        arguments = listOf(
            navArgument("itemId"){
                type= NavType.IntType
            },
            navArgument("filterType"){
                type= NavType.IntType
            },
            navArgument("searchQuery"){
                type= NavType.StringType
            }
        )
    ) {
        BookDetailScreen(
            navController = navController,
            sharedTransitionScope = sharedTransitionScope,
            onReadBtnClicked = onReadBtnClicked,
            onMemoAddBtnClicked = onMemoAddBtnClicked,
            onMemoEditBtnClicked = onMemoEditBtnClicked,
            animatedVisibilityScope = this,
        )
    }
}