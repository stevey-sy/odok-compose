package com.sy.feature.memo

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sy.core.ui.toast
import com.sy.odokcompose.core.designsystem.DashiFont
import com.sy.odokcompose.core.designsystem.MaruBuriFont
import com.sy.odokcompose.core.designsystem.OdokColors
import com.sy.odokcompose.core.designsystem.OdokTheme
import com.sy.odokcompose.core.designsystem.icon.OdokIcons
import kotlinx.coroutines.launch

@Composable
fun AddMemoScreen(
    onClose: () -> Unit,
    viewModel: AddMemoViewModel = hiltViewModel(),
    handleEvent: (AddMemoEvent) -> Unit = viewModel::handleEvent
) {
    val pageTextState by viewModel.pageText.collectAsState()
    val memoTextState by viewModel.memoText.collectAsState()
    val selectedPaperType by viewModel.selectedPaperType.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val paperTypes = listOf(
        "white_paper" to OdokIcons.WhitePaper,
        "old_paper" to OdokIcons.OldPaper,
        "dot_paper" to OdokIcons.DotPaper,
        "blue_sky" to OdokIcons.BlueSky,
        "yellow_paper" to OdokIcons.YellowPaper
    )
    val pagerState = rememberPagerState(pageCount = { paperTypes.size })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        viewModel.updatePaperType(pagerState.currentPage)
    }

    // 저장 성공 시 Snackbar 표시 및 메인 화면으로 이동
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                AddMemoEvent.HandleCloseButton -> onClose()
                is AddMemoEvent.ShowMessage -> context.toast(event.message)
                AddMemoEvent.MemoSaved -> {
                    context.toast("메모가 저장되었습니다.")
                    onClose()
                }
                else -> {}
            }
        }
    }

    OdokTheme {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                Column {
                    LazyRow(
                        modifier = Modifier
                            .background(OdokColors.Black)
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(paperTypes) { (paperTypeName, paperTypeId) ->
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .shadow(4.dp, RoundedCornerShape(8.dp))
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .clickable { 
                                        viewModel.updatePaperType(paperTypes.indexOfFirst { it.first == paperTypeName })
                                    }
                            ) {
                                Image(
                                    painter = painterResource(id = paperTypeId),
                                    contentDescription = "메모지 선택",
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                )
                                
                                if (paperTypeName == selectedPaperType) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.3f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "선택됨",
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Button(
                        onClick = { 
                            if (!isSaving) {
                                handleEvent(AddMemoEvent.HandleSaveButton)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OdokColors.Black
                        ),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Icon(Icons.Default.Check, contentDescription = "완료")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("완료")
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(OdokColors.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        IconButton(onClick = { handleEvent(AddMemoEvent.HandleCloseButton) }) {
                            Image(
                                painter = painterResource(id = OdokIcons.CloseButton),
                                contentDescription = "닫기",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    Text(
                        modifier = Modifier.padding(top = 20.dp),
                        text = when (uiState) {
                            is AddMemoUiState.CreateMode -> "메모를 추가합니다."
                            is AddMemoUiState.EditMode -> "메모를 수정합니다."
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = OdokColors.Black
                    )

                    Text(
                        text = "책에서 읽은 문구나 생각을 저장하세요",
                        modifier = Modifier.padding(top = 4.dp),
                        fontSize = 14.sp,
                        color = OdokColors.StealGray,
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                            .wrapContentHeight()
                            .shadow(8.dp, RoundedCornerShape(10.dp))
                            .background(Color.White, RoundedCornerShape(10.dp))
                    ) {
                        Image(
                            painter = painterResource(id = when(selectedPaperType) {
                                "white_paper" -> OdokIcons.WhitePaper
                                "old_paper" -> OdokIcons.OldPaper
                                "dot_paper" -> OdokIcons.DotPaper
                                "blue_sky" -> OdokIcons.BlueSky
                                "yellow_paper" -> OdokIcons.YellowPaper
                                else -> OdokIcons.WhitePaper
                            }),
                            contentDescription = "메모하기",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.matchParentSize()
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "p.",
                                fontSize = 16.sp,
                                fontFamily = MaruBuriFont,
                                color = OdokColors.Black
                            )
                            TextField(
                                value = pageTextState,
                                onValueChange = { viewModel.updatePageText(it) },
                                placeholder = {
                                    Text(
                                        text = "페이지 입력",
                                        color = OdokColors.StealGray,
                                        fontSize = 16.sp,
                                        fontFamily = MaruBuriFont,
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                textStyle = TextStyle(
                                    color = OdokColors.Black,
                                    fontSize = 16.sp,
                                    fontFamily = MaruBuriFont,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Start
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 240.dp)
                                .padding(horizontal = 24.dp, vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            TextField(
                                value = memoTextState,
                                onValueChange = { viewModel.updateMemoText(it) },
                                placeholder = {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "메모를 입력하세요",
                                            color = OdokColors.StealGray,
                                            fontSize = 22.sp,
                                            fontFamily = DashiFont,
                                            fontWeight = FontWeight.Normal,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                textStyle = TextStyle(
                                    color = OdokColors.Black,
                                    fontSize = 22.sp,
                                    fontFamily = DashiFont,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center
                                ),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Default
                                ),
                                singleLine = false,
                                maxLines = Int.MAX_VALUE,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(40.dp)
                                    .align(Alignment.Center)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp)
                                .align(Alignment.BottomCenter),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = viewModel.displayDate,
                                fontSize = 14.sp,
                                fontFamily = MaruBuriFont,
                                fontWeight = FontWeight.Normal,
                                color = OdokColors.StealGray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}