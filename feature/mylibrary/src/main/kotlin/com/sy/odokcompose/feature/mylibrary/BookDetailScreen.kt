package com.sy.odokcompose.feature.mylibrary

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sy.odokcompose.core.designsystem.OdokTheme
import com.sy.odokcompose.core.designsystem.OdokColors
import com.sy.odokcompose.feature.mylibrary.components.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun BookDetailScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: BookDetailViewModel = hiltViewModel(),
) {
    val bookList by viewModel.bookList.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val currentBook by viewModel.currentBook.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val finishedReadCnt by viewModel.finishedReadCnt.collectAsState()
    val currentPageCnt by viewModel.currentPageCnt.collectAsState()

    val sheetState = rememberModalBottomSheetState()

    OdokTheme {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(OdokColors.White)
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    BookPager(
                        bookList = bookList,
                        currentPage = currentPage,
                        onPageChanged = viewModel::onPageChanged,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )

                    BookInfo(
                        title = currentBook?.title ?: "",
                        author = currentBook?.author ?: ""
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BookProgress(
                        progressPercentage = currentBook?.progressPercentage ?: 0,
                        progressText = currentBook?.progressText ?: ""
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BookActionButtons()

                    CommentSection()
                }

                if (uiState.isEditViewShowing) {
                    ModalBottomSheet(
                        onDismissRequest = { viewModel.hideEditView() },
                        sheetState = sheetState,
                        modifier = Modifier.fillMaxHeight(1.0f),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                                .navigationBarsPadding()
                                .imePadding()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = "독서 정보 수정",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = OdokColors.Black
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // 완독 횟수 수정
                            Text(
                                text = "완독 횟수",
                                fontSize = 16.sp,
                                color = OdokColors.Black
                            )
                            Spacer(modifier = Modifier.height(8.dp))
//                            OutlinedTextField(
//                                value = finishedReadCnt,
//                                onValueChange = { viewModel.updateFinishedReadCnt(it) },
//                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                                modifier = Modifier.fillMaxWidth()
//                            )
                            var finishedCntValue by remember { mutableStateOf(TextFieldValue(finishedReadCnt)) }
                            OutlinedTextField(
                                value = finishedCntValue,
                                onValueChange = {
                                    finishedCntValue = it
                                    viewModel.updateFinishedReadCnt(it.text)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = TextStyle(
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier
                                    .fillMaxWidth(0.35f)
                                    .onFocusChanged { focusState ->
                                        if (focusState.isFocused) {
                                            finishedCntValue = finishedCntValue.copy(
                                                selection = TextRange(0, finishedCntValue.text.length)
                                            )
                                        }
                                    }
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))

                            var currentCntValue by remember { mutableStateOf(TextFieldValue(currentPageCnt)) }
                            // 현재 페이지 수정
                            Text(
                                text = "현재 페이지",
                                fontSize = 16.sp,
                                color = OdokColors.Black
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(0.5f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = currentCntValue,
                                    onValueChange = {
                                        currentCntValue = it
                                        viewModel.updateFinishedReadCnt(it.text)
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(0.7f),
                                    textStyle = TextStyle(
                                        textAlign = TextAlign.Center
                                    ),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "/ ${currentBook?.totalPageCnt ?: 0}",
                                    fontSize = 16.sp,
                                    color = OdokColors.Black
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // 저장 버튼
                            Button(
                                onClick = { viewModel.saveChanges() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = OdokColors.Black
                                )
                            ) {
                                Text("저장")
                            }
                            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.ime))
                        }
                    }
                }
            }
        }
    }
}