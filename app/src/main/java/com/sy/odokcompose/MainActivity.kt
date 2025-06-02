package com.sy.odokcompose

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.sy.odokcompose.navigation.BottomNavItem
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.sy.odokcompose.feature.search.navigation.SEARCH_BOOK_DETAIL_ROUTE
import com.sy.odokcompose.feature.search.navigation.searchBookDetailScreen
import com.sy.odokcompose.feature.search.navigation.searchBookScreen
import com.sy.odokcompose.feature.search.navigation.navigateToSearchBookDetail
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sy.odokcompose.core.designsystem.icon.OdokIcons
import com.sy.odokcompose.feature.mylibrary.navigation.BOOK_DETAIL_ROUTE
import com.sy.odokcompose.feature.mylibrary.navigation.bookDetailScreen
import com.sy.odokcompose.feature.mylibrary.navigation.navigateToBookDetail
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sy.odokcompose.feature.mylibrary.MyLibraryViewModel
import com.sy.odokcompose.model.type.ShelfFilterType

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

@SuppressLint("UnrememberedGetBackStackEntry")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val navController = rememberNavController()
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
                modifier = Modifier.fillMaxSize()
                    .background(Color.White),
                containerColor = Color.White,
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
                    }
                    else if (currentRoute?.contains(SEARCH_BOOK_DETAIL_ROUTE) == true) {
                        OdokTopAppBar(
                            title = "서재 등록",
                            showBackButton = true,
                            fontFamily = FontFamily.Default,
                            fontSize = 22.sp,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    else if (currentRoute?.contains(BOOK_DETAIL_ROUTE) == true) {
                        OdokTopAppBar(
                            title = "오독오독",
                        )
                    }
                    else if (currentRoute == MY_LIBRARY_ROUTE) {
                        // 일반 화면일 때는 타이틀과 검색 버튼이 있는 TopAppBar 표시

                        val myLibraryBackStackEntry = remember { navController.getBackStackEntry(MY_LIBRARY_ROUTE) }
                        val myLibraryViewModel: MyLibraryViewModel = hiltViewModel(myLibraryBackStackEntry)

                        OdokTopAppBar(
                            title = "오독오독",
                            actions = {

                                ActionIconButton(
                                    onClick = { 
                                        myLibraryViewModel.toggleSearchView(true)
                                    },
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "검색"
                                )

                                var expanded by remember { mutableStateOf(false) }
                                val currentFilter by myLibraryViewModel.currentFilter.collectAsState()

                                Box {
                                    IconButton(onClick = { expanded = true }) {
                                        Icon(
                                            painter = painterResource(id = OdokIcons.Filter),
                                            contentDescription = "필터",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("전체") },
                                            leadingIcon = {
                                                if (currentFilter == ShelfFilterType.NONE) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "선택됨"
                                                    )
                                                }
                                            },
                                            onClick = {
                                                myLibraryViewModel.updateFilter(ShelfFilterType.NONE)
                                                expanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("읽는 중") },
                                            leadingIcon = {
                                                if (currentFilter == ShelfFilterType.READING) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "선택됨"
                                                    )
                                                }
                                            },
                                            onClick = {
                                                myLibraryViewModel.updateFilter(ShelfFilterType.READING)
                                                expanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("완독") },
                                            leadingIcon = {
                                                if (currentFilter == ShelfFilterType.FINISHED) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "선택됨"
                                                    )
                                                }
                                            },
                                            onClick = {
                                                myLibraryViewModel.updateFilter(ShelfFilterType.FINISHED)
                                                expanded = false
                                            }
                                        )
                                    }
                                }

                                IconButton(onClick = { navController.navigateToSearch() }) {
                                    Icon(
                                        painter = painterResource(id = OdokIcons.Plus),
                                        contentDescription = "추가",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        )
                    }
                },
                bottomBar = {
                    // 현재 경로가 검색 화면이 아닐 때만 바텀 네비게이션 표시
                    if (currentRoute == MY_LIBRARY_ROUTE) {
                        OdokBottomNavigationBar(navController = navController, items = navigationItems)
                    } else if (currentRoute?.contains(BOOK_DETAIL_ROUTE) == true) {
                        OdokBottomNavigationBar(navController = navController, items = navigationItems)
                    }
                },
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    SharedTransitionLayout {
                        NavigationGraph(navController = navController, sharedTransitionScope = this)
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedSharedTransitionModifierParameter")
@OptIn(ExperimentalAnimationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun NavigationGraph(navController: NavHostController, sharedTransitionScope: SharedTransitionScope) {
    NavHost(
        navController = navController,
        startDestination = MY_LIBRARY_ROUTE
    ) {
        myLibraryScreen(
            sharedTransitionScope = sharedTransitionScope,
            onNavigateToSearch = { navController.navigateToSearch()},
            onBookItemClicked = { itemId ->
                navController.navigateToBookDetail(itemId)
            }
        )

        bookDetailScreen(
            sharedTransitionScope = sharedTransitionScope,
        )

        searchBookScreen(
            sharedTransitionScope = sharedTransitionScope,
            onNavigateBack = { navController.popBackStack()},
            onNavigateToDetail = { isbn, cover ->
                navController.navigateToSearchBookDetail(isbn, cover)
            }
        )

        searchBookDetailScreen(
            sharedTransitionScope = sharedTransitionScope,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToMain = {
                // 모든 백스택을 지우고 메인 화면으로 이동
                navController.navigate(MY_LIBRARY_ROUTE) {
                    popUpTo(MY_LIBRARY_ROUTE) { inclusive = true }
                }
            }
        )
    }
}