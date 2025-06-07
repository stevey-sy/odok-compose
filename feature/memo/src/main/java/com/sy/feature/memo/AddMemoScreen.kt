package com.sy.feature.memo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sy.odokcompose.core.designsystem.OdokColors
import com.sy.odokcompose.core.designsystem.OdokTheme
import com.sy.odokcompose.core.designsystem.icon.OdokIcons
import androidx.compose.ui.text.style.TextAlign
import com.sy.odokcompose.core.designsystem.MaruBuriFont

@Composable
fun AddMemoScreen(
    onClose: () -> Unit,
    viewModel: AddMemoViewModel = hiltViewModel()
) {
    val memoText = viewModel.memoText
    OdokTheme {
        Scaffold (
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(OdokColors.White)
            ) {
                Column (
                    modifier = Modifier
                        .padding(12.dp)
                ){
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        IconButton(onClick = { onClose }) {
                            Image(
                                painter = painterResource(id = OdokIcons.CloseButton),
                                contentDescription = "닫기",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    // 메모를 추가합니다.
                    Text(
                        modifier = Modifier.padding(top=20.dp),
                        text = "메모를 추가합니다.",
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
                            .padding(top=20.dp)
                            .height(240.dp)
                            .shadow(8.dp, RoundedCornerShape(10.dp))
                            .background(Color.White, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = OdokIcons.WhitePaper),
                            contentDescription = "메모하기",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .fillMaxSize()
                        )

                        TextField(
                            value = memoText,
                            onValueChange = { viewModel.updateMemoText(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            placeholder = {
                                Text(
                                    text = "메모를 입력하세요",
                                    color = OdokColors.StealGray,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = OdokColors.Black,
                                unfocusedIndicatorColor = OdokColors.Black.copy(alpha = 0.5f)
                            ),
                            textStyle = TextStyle(
                                color = OdokColors.Black,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Default
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    // 원하는 동작 수행
                                }
                            ),
                            singleLine = false,
                            maxLines = 5
                        )

                        Text(
                            text = "2025. 06. 07",
                            modifier = Modifier
                                .padding(top = 24.dp),
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