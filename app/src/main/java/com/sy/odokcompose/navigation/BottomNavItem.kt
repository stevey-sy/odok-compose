package com.sy.odokcompose.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        title = "홈",
        icon = Icons.Default.Home
    )
    
    object Library : BottomNavItem(
        route = "library",
        title = "서재",
        icon = Icons.Default.Home
    )
    
    object Profile : BottomNavItem(
        route = "profile",
        title = "프로필",
        icon = Icons.Default.Person
    )
} 