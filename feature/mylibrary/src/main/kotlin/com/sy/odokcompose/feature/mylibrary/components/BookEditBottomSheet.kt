package com.sy.odokcompose.feature.mylibrary.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sy.odokcompose.core.designsystem.OdokColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookEditBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    finishedReadCnt: String,
    currentPageCnt: String,
    totalPageCnt: Int,
    startDate: LocalDate,
    endDate: LocalDate?,
    onFinishedReadCntChange: (String) -> Unit,
    onCurrentPageCntChange: (String) -> Unit,
    onStartDateChange: (LocalDate) -> Unit,
    onEndDateChange: (LocalDate?) -> Unit,
    onSaveClick: () -> Unit
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy.MM.dd") }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
//                .navigationBarsPadding()
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
            
            // 시작일 수정
            Text(
                text = "시작일",
                fontSize = 16.sp,
                color = OdokColors.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = startDate.format(dateFormatter),
                onValueChange = { },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            showStartDatePicker = true
                        }
                    },
                textStyle = TextStyle(
                    textAlign = TextAlign.Center
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 종료일 수정
            Text(
                text = "종료일",
                fontSize = 16.sp,
                color = OdokColors.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = endDate?.format(dateFormatter) ?: "",
                onValueChange = { },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            showEndDatePicker = true
                        }
                    },
                textStyle = TextStyle(
                    textAlign = TextAlign.Center
                ),
                singleLine = true,
                placeholder = { Text("미정") }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 완독 횟수 수정
            Text(
                text = "완독 횟수",
                fontSize = 16.sp,
                color = OdokColors.Black
            )
            Spacer(modifier = Modifier.height(8.dp))

            var finishedCntValue by remember { mutableStateOf(TextFieldValue(finishedReadCnt)) }
            OutlinedTextField(
                value = finishedCntValue,
                onValueChange = {
                    finishedCntValue = it
                    onFinishedReadCntChange(it.text)
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
                        onCurrentPageCntChange(it.text)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                currentCntValue = currentCntValue.copy(
                                    selection = TextRange(0, currentCntValue.text.length)
                                )
                            }
                        },
                    textStyle = TextStyle(
                        textAlign = TextAlign.Center
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "/ $totalPageCnt",
                    fontSize = 16.sp,
                    color = OdokColors.Black
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 저장 버튼
            Button(
                onClick = onSaveClick,
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
    
    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("확인")
                }
            }
        ) {
            DatePicker(
                state = rememberDatePickerState(
                    initialSelectedDateMillis = startDate.toEpochDay() * 24 * 60 * 60 * 1000
                ),
                title = { Text("시작일 선택") },
                headline = { Text("시작일을 선택해주세요") },
                showModeToggle = false
            )
        }
    }
    
    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("확인")
                }
            }
        ) {
            DatePicker(
                state = rememberDatePickerState(
                    initialSelectedDateMillis = endDate?.toEpochDay()?.times(24 * 60 * 60 * 1000)
                ),
                title = { Text("종료일 선택") },
                headline = { Text("종료일을 선택해주세요") },
                showModeToggle = false
            )
        }
    }
} 