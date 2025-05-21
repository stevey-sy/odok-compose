package com.sy.odokcompose.feature.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sy.odokcompose.feature.search.SearchScreen

const val SEARCH_ROUTE = "search"
const val SEARCH_DETAIL_ROUTE = "search_detail"

fun NavGraphBuilder.searchScreen(
    onNavigateBack: () -> Unit
) {
    composable(route = SEARCH_ROUTE) {
        SearchScreen(onNavigateBack = onNavigateBack)
    }
    composable(route = SEARCH_DETAIL_ROUTE) {
        SearchScreen(onNavigateBack = onNavigateBack)
    }
}

fun NavController.navigateToSearch(navOptions: NavOptions? = null) {
    this.navigate(SEARCH_ROUTE, navOptions)
}

fun NavController.navigateToSearchDetail(navOptions: NavOptions? = null) {
    this.navigate(SEARCH_DETAIL_ROUTE, navOptions)
}