package com.sy.odokcompose.feature.mylibrary

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
//import com.sy.odokcompose.ui.theme.DashiFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyLibraryScreen(
    viewModel: MyLibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val drawableId = context.resources.getIdentifier("ic_plus_24", "drawable", context.packageName)
    
    Scaffold(
        topBar = { 
            TopAppBar(
                title = { 
                    Text(
                        text = "오독오독", 
//                        fontFamily = DashiFont,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Normal
                    ) 
                },
                actions = {
                    IconButton(onClick = { 
                        // 버튼 액션
                    }) {
                        Icon(
                            painter = painterResource(id = drawableId),
                            contentDescription = "추가"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "내 서재",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}