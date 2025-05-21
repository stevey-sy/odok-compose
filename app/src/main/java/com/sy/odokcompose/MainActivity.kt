package com.sy.odokcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sy.odokcompose.core.designsystem.OdokTheme
import com.sy.odokcompose.core.designsystem.component.ActionIconButton
import com.sy.odokcompose.core.designsystem.component.BottomNavigationItem
import com.sy.odokcompose.core.designsystem.component.OdokBottomNavigationBar
import com.sy.odokcompose.core.designsystem.component.OdokTopAppBar
import com.sy.odokcompose.feature.mylibrary.navigation.MY_LIBRARY_ROUTE
import com.sy.odokcompose.feature.mylibrary.navigation.myLibraryScreen
import com.sy.odokcompose.feature.search.navigation.SEARCH_ROUTE
import com.sy.odokcompose.feature.search.navigation.navigateToSearch
import com.sy.odokcompose.feature.search.navigation.searchScreen
import com.sy.odokcompose.navigation.BottomNavItem
import com.sy.odokcompose.screens.HomeScreen
import com.sy.odokcompose.screens.LibraryScreen
import com.sy.odokcompose.screens.ProfileScreen
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 시스템 UI가 앱 콘텐츠 위에 그려지도록 설정
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Edge-to-Edge 설정
        enableEdgeToEdge()
        
        setContent {
            OdokTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val context = LocalContext.current
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    
    // 하단 네비게이션 아이템 정의
    val navigationItems = listOf(
        BottomNavigationItem(
            route = MY_LIBRARY_ROUTE,
            title = "내 서재",
            icon = Icons.Default.Home
        ),
        BottomNavigationItem(
            route = BottomNavItem.Home.route,
            title = "홈",
            icon = Icons.Default.Home
        ),
        BottomNavigationItem(
            route = BottomNavItem.Library.route,
            title = "서재",
            icon = Icons.Default.Home
        ),
        BottomNavigationItem(
            route = BottomNavItem.Profile.route,
            title = "프로필",
            icon = Icons.Default.Person
        )
    )
    
    when (uiState) {
        is MainUiState.Loading -> {
            // 로딩 중일 때 로딩 인디케이터 표시
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        is MainUiState.Success -> {
            // 로딩 완료 후 메인 UI 표시
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    // 검색 화면일 때는 뒤로가기 버튼이 있는 TopAppBar 표시
                    if (currentRoute == SEARCH_ROUTE) {
                        OdokTopAppBar(
                            title = "도서검색",
                            showBackButton = true,
                            fontFamily = FontFamily.Default,
                            fontSize = 22.sp,
                            onBackClick = { navController.popBackStack() }
                        )
                    } else {
                        // 일반 화면일 때는 타이틀과 검색 버튼이 있는 TopAppBar 표시
                        val drawableId = context.resources.getIdentifier("ic_plus_24", "drawable", context.packageName)
                        OdokTopAppBar(
                            title = "오독오독",
                            actions = {
                                ActionIconButton(
                                    onClick = { navController.navigateToSearch() },
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "검색"
                                )
                            }
                        )
                    }
                },
                bottomBar = {
                    // 현재 경로가 검색 화면이 아닐 때만 바텀 네비게이션 표시
                    if (currentRoute != SEARCH_ROUTE) {
                        OdokBottomNavigationBar(navController = navController, items = navigationItems)
                    }
                },
//                contentWindowInsets = WindowInsets(0, 0, 0, 0)
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    NavigationGraph(navController = navController)
                }
            }
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController, 
        startDestination = MY_LIBRARY_ROUTE
    ) {
        myLibraryScreen(
            onNavigateToSearch = { navController.navigateToSearch() }
        )
        searchScreen(
            onNavigateBack = { navController.popBackStack() }
        )
        composable(BottomNavItem.Home.route) {
            HomeScreen()
        }
        composable(BottomNavItem.Library.route) {
            LibraryScreen()
        }
        composable(BottomNavItem.Profile.route) {
            ProfileScreen()
        }
    }
}