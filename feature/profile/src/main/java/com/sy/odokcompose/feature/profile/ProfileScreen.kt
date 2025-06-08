package com.sy.odokcompose.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sy.odokcompose.core.designsystem.OdokColors
import com.sy.odokcompose.core.designsystem.OdokTheme

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val exportState by viewModel.exportState.collectAsState()

    OdokTheme {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(OdokColors.White)
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "프로필",
                    style = MaterialTheme.typography.headlineMedium
                )

                Button(
                    onClick = { viewModel.exportDatabase() },
                    enabled = exportState !is ExportState.Exporting
                ) {
                    Text("데이터 내보내기")
                }

                when (exportState) {
                    is ExportState.Exporting -> {
                        CircularProgressIndicator()
                    }
                    is ExportState.Success -> {
                        val file = (exportState as ExportState.Success).file
                        Text(
                            text = "데이터가 ${file.name}에 저장되었습니다",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    is ExportState.Error -> {
                        val message = (exportState as ExportState.Error).message
                        Text(
                            text = "오류: $message",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    else -> Unit
                }
            }
        }
    }
}