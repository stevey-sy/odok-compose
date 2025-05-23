package com.sy.odokcompose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.*
import com.sy.odokcompose.feature.mylibrary.MyLibraryScreen
import com.sy.odokcompose.feature.search.SearchScreen

@Composable
fun AppNavGraph(navController: NavController) {
    NavHost(navController = navController as NavHostController, startDestination = "myLibrary") {
        composable("mylibrary") {
            MyLibraryScreen(
                onNavigateToSearch =  {
                navController.navigate("search")
                }
            )
        }
        composable("search") {
            SearchScreen(onNavigateBack = {
                navController.popBackStack()
            })
        }
    }
}