package com.sy.odokcompose.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * 오독오독 앱에서 사용하는 하단 네비게이션 바
 */
@Composable
fun OdokBottomNavigationBar(
    navController: NavController,
    items: List<BottomNavigationItem>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    NavigationBar(
        modifier = Modifier.fillMaxWidth()
    ) {
        items.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { 
                it.route == item.route 
            } == true
            
            NavigationBarItem(
                icon = { 
                    Icon(
                        imageVector = item.icon, 
                        contentDescription = item.title
                    ) 
                },
                label = { Text(text = item.title) },
                selected = selected,
                onClick = {
                    if (currentDestination?.route != item.route) {
                        navController.navigate(item.route) {
                            // 백 스택에 같은 화면이 여러 번 쌓이는 것을 방지
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // 같은 화면을 여러 번 클릭해도 한 번만 화면을 쌓음
                            launchSingleTop = true
                            // 이전 상태 복원
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

/**
 * 하단 네비게이션 아이템 데이터 클래스
 */
data class BottomNavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

