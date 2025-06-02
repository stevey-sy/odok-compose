package com.sy.odokcompose.core.designsystem.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.sy.odokcompose.core.designsystem.OdokColors

@Composable
fun SearchTextField(
    query: String,
    hint: String = "내 서재에 저장된 책을 검색합니다.",
    focusManager: FocusManager,
    onKeyTypes: (query:String) -> Unit = {},
    onSearchClicked: (query:String) -> Unit = {},
    onClose: () -> Unit = {}
) {
    TextField(
        value = query,
        onValueChange = {
            onKeyTypes(it)
        },
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        placeholder = { Text(hint) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "검색"
            )
        },
        trailingIcon = {
            Row {
//                if (query.isNotEmpty()) {
//                    IconButton(onClick = {
////                        viewModel.updateSearchQuery("")
//                    }) {
//                        Icon(
//                            imageVector = Icons.Default.Clear,
//                            contentDescription = "지우기"
//                        )
//                    }
//                }
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "닫기"
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearchClicked(query)
//                viewModel.search(query)
                focusManager.clearFocus()
            }
        ),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedPlaceholderColor = OdokColors.StealGray,
            unfocusedPlaceholderColor = OdokColors.StealGray,
        )
    )
}
