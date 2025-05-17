package com.sy.odokcompose.feature.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sy.odokcompose.feature.search.SearchScreen

const val SEARCH_ROUTE = "search"

fun NavGraphBuilder.searchScreen(
    onNavigateBack: () -> Unit
) {
    composable(route = SEARCH_ROUTE) {
        SearchScreen(onNavigateBack = onNavigateBack)
    }
}

fun NavController.navigateToSearch(navOptions: NavOptions? = null) {
    this.navigate(SEARCH_ROUTE, navOptions)
}