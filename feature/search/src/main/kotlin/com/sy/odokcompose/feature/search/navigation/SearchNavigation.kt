package com.sy.odokcompose.feature.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sy.odokcompose.feature.search.SearchScreen
import com.sy.odokcompose.feature.search.SearchBookDetailScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

const val SEARCH_ROUTE = "search"
const val SEARCH_BOOK_DETAIL_ROUTE = "search_book_detail"

fun NavController.navigateToSearch(navOptions: NavOptions? = null) {
    this.navigate(SEARCH_ROUTE, navOptions)
}

fun NavController.navigateToSearchBookDetail(isbn: String, cover: String, navOptions: NavOptions? = null) {
    // URL 인코딩을 통해 특수 문자가 있는 URL 처리
    val encodedCover = URLEncoder.encode(cover, StandardCharsets.UTF_8.toString())
    this.navigate("$SEARCH_BOOK_DETAIL_ROUTE/$isbn/$encodedCover", navOptions)
}

fun NavGraphBuilder.searchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (isbn: String, cover: String) -> Unit
) {
    composable(route = SEARCH_ROUTE) {
        SearchScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToDetail = onNavigateToDetail
        )
    }
}

fun NavGraphBuilder.searchBookDetailScreen(
    onNavigateBack: () -> Unit,
    route: String = "$SEARCH_BOOK_DETAIL_ROUTE/{isbn}/{cover}"
) {
    composable(
        route = route,
        arguments = listOf(
            // 필요한 경우 NavType 지정 가능
        )
    ) { backStackEntry ->
        val isbn = backStackEntry.arguments?.getString("isbn") ?: ""
        val encodedCover = backStackEntry.arguments?.getString("cover") ?: ""
        val cover = java.net.URLDecoder.decode(encodedCover, StandardCharsets.UTF_8.toString())
        
        SearchBookDetailScreen(
            isbn = isbn,
            cover = cover,
            onNavigateBack = onNavigateBack
        )
    }
}