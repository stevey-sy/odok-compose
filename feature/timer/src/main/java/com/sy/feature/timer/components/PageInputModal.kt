package com.sy.feature.timer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sy.feature.timer.TimerViewModel
import com.sy.odokcompose.core.designsystem.OdokColors
import com.sy.odokcompose.model.BookUiModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PageInputModal(
    isPageInputModalVisible: Boolean,
    viewModel: TimerViewModel,
    book: BookUiModel,
    sheetState: SheetState,
    lastReadPageInput: String
) {
    if (isPageInputModalVisible) {
        LaunchedEffect(Unit) {
            // 모달이 처음 열릴 때만 초기값 설정
            viewModel.onLastReadPageInputChange(book.currentPageCnt.toString())
        }
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissPageInputModal() },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("마지막으로 읽은 페이지를 입력하세요")
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("p.")
                    TextField(
                        value = lastReadPageInput,
                        onValueChange = { viewModel.onLastReadPageInputChange(it) },
                        modifier = Modifier.width(100.dp), // 입력칸 너비 조절
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(textAlign = TextAlign.Center)
                        // label = { Text("페이지") } // 라벨은 Row 바깥으로 빼거나 다른 형태로 표시
                    )
                    Text(" / ${book.totalPageCnt}")
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.saveLastReadPageAndDismiss() },
                    enabled = lastReadPageInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OdokColors.Black, // 원하는 배경색
                        contentColor = Color.White          // 텍스트 색
                    )
                ) {
                    Text("저장")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}