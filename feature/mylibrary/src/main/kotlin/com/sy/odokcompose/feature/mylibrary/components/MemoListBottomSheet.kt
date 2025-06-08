package com.sy.odokcompose.feature.mylibrary.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sy.odokcompose.core.designsystem.DashiFont
import com.sy.odokcompose.core.designsystem.MaruBuriFont
import com.sy.odokcompose.core.designsystem.OdokColors
import com.sy.odokcompose.core.designsystem.icon.OdokIcons
import com.sy.odokcompose.feature.mylibrary.BookDetailViewModel
import com.sy.odokcompose.model.MemoUiModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MemoListBottomSheet(
    sheetState: SheetState,
    memoList: List<MemoUiModel>,
    onDismissRequest: () -> Unit,
    onEditClick: (memoId: Int) -> Unit,
    onDeleteClick: (memoId: Int) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            items(memoList) { memo ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, start = 24.dp, end = 24.dp, bottom = 20.dp)
                        .wrapContentHeight()
                        .heightIn(min = 240.dp)
                        .shadow(8.dp, RoundedCornerShape(10.dp))
                        .background(Color.White, RoundedCornerShape(10.dp))
                ) {

                    Image(
                        painter = painterResource(
                            id = when (memo.backgroundId) {
                                "white_paper" -> OdokIcons.WhitePaper
                                "old_paper" -> OdokIcons.OldPaper
                                "dot_paper" -> OdokIcons.DotPaper
                                "blue_sky" -> OdokIcons.BlueSky
                                "yellow_paper" -> OdokIcons.YellowPaper
                                else -> OdokIcons.WhitePaper
                            }
                        ),
                        contentDescription = "메모 배경",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.matchParentSize()
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(horizontal = 24.dp, vertical = 20.dp)
                    ) {
                        // 상단 페이지 정보
                        Row {
                            Text(
                                text = "p.${memo.pageNumber}",
                                fontSize = 16.sp,
                                fontFamily = MaruBuriFont,
                                color = if (memo.pageNumber == 0) Color.Transparent else Color.Unspecified
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 중앙 Box: 한 줄이어도 정중앙에 배치되도록
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp)
                                .wrapContentHeight(),
                            contentAlignment = Alignment.Center // 👈 핵심
                        ) {
                            Text(
                                text = memo.content,
                                fontSize = 22.sp,
                                fontFamily = DashiFont,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                color = Color.Black,
                                softWrap = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(horizontal = 16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 하단 날짜 정보
                        Text(
                            text = memo.getCreateDateText(),
                            fontSize = 14.sp,
                            fontFamily = MaruBuriFont,
                            color = OdokColors.StealGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        var showMenu by remember { mutableStateOf(false) }
                        var iconOffset by remember { mutableStateOf(Offset.Zero) }
                        val density = LocalDensity.current
                        val iconSize = 24.dp

                        Box(
                            modifier = Modifier
                                .onGloballyPositioned { coordinates ->
                                    iconOffset = coordinates.positionInWindow()
                                }
                        ) {
                            Image(
                                painter = painterResource(id = OdokIcons.HorizontalDots),
                                contentDescription = "메모 설정",
                                modifier = Modifier
                                    .size(iconSize)
                                    .clickable { showMenu = true }
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            offset = DpOffset(
                                x = with(density) { iconOffset.x.toDp() - iconSize },
                                y = with(density) { 0.dp }
                            )
                        ) {
                            DropdownMenuItem(
                                text = { Text("수정") },
                                onClick = {
                                    showMenu = false
                                    onEditClick(memo.memoId)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("삭제") },
                                onClick = {
                                    showMenu = false
                                    onDeleteClick(memo.memoId)
                                }
                            )
                        }
                    }

                }

            }
        }
    }
}