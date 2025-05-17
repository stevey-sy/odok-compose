package com.sy.odokcompose.feature.mylibrary.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sy.odokcompose.feature.mylibrary.MyLibraryScreen

const val MY_LIBRARY_ROUTE = "my_library"

fun NavGraphBuilder.myLibraryScreen(
    onNavigateToSearch: () -> Unit
) {
    composable(route = MY_LIBRARY_ROUTE) {
        MyLibraryScreen(onNavigateToSearch = onNavigateToSearch)
    }
}

fun NavController.navigateToMyLibrary() {
    this.navigate(MY_LIBRARY_ROUTE)
}