package com.sy.odokcompose.feature.search.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sy.odokcompose.feature.search.SearchBookScreen
import com.sy.odokcompose.feature.search.SearchBookDetailScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

const val SEARCH_ROUTE = "search"
const val SEARCH_BOOK_DETAIL_ROUTE = "search_detail"

fun NavController.navigateToSearch(navOptions: NavOptions? = null) {
    this.navigate(SEARCH_ROUTE, navOptions)
}

fun NavController.navigateToSearchBookDetail(isbn: String, cover: String, navOptions: NavOptions? = null) {
    // URL 인코딩을 통해 특수 문자가 있는 URL 처리
    val encodedCover = URLEncoder.encode(cover, StandardCharsets.UTF_8.toString())
    this.navigate("$SEARCH_BOOK_DETAIL_ROUTE/$isbn/$encodedCover", navOptions)
}


@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.searchBookScreen(
    sharedTransitionScope: SharedTransitionScope,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (isbn: String, cover: String) -> Unit,
) {
    composable(SEARCH_ROUTE) {
        SearchBookScreen(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this,
            onNavigateBack = onNavigateBack,
            onNavigateToDetail = onNavigateToDetail,
        )
    }
}


@SuppressLint("ComposableDestinationInComposeScope")
@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.searchBookDetailScreen(
    sharedTransitionScope: SharedTransitionScope,
    onNavigateBack: () -> Unit,
    onNavigateToMain: () -> Unit,
    route: String = "$SEARCH_BOOK_DETAIL_ROUTE/{isbn}/{cover}"
) {
    composable(
        route = route,
        arguments = listOf(
            // 필요한 경우 NavType 지정 가능
            navArgument("isbn"){
                type = NavType.StringType
            },
            navArgument("cover"){
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->
        val isbn = backStackEntry.arguments?.getString("isbn") ?: ""
        val encodedCover = backStackEntry.arguments?.getString("cover") ?: ""
        val cover = URLDecoder.decode(encodedCover, StandardCharsets.UTF_8.toString())

        SearchBookDetailScreen(
            isbn = isbn,
            cover = cover,
            onNavigateBack = onNavigateBack,
            sharedTransitionScope = sharedTransitionScope,
            onNavigateToMain = onNavigateToMain,
            animatedVisibilityScope = this
        )
    }

}