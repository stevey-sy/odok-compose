package com.sy.odokcompose.feature.mylibrary

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sy.odokcompose.core.designsystem.OdokTheme
import com.sy.odokcompose.feature.mylibrary.components.BookCover
import com.sy.odokcompose.feature.mylibrary.components.BookShelfBase
import com.sy.odokcompose.feature.mylibrary.components.BookShelfLeft
import com.sy.odokcompose.feature.mylibrary.components.BookShelfRight
import com.sy.odokcompose.model.BookUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyLibraryScreen(
    onNavigateToSearch: () -> Unit,
    viewModel: MyLibraryViewModel = hiltViewModel()
) {
    val shelfItems by viewModel.shelfItems.collectAsState()
    
    OdokTheme {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    content = {
                        itemsIndexed(shelfItems) { index, book ->
                            BookShelfItem(
                                index = index,
                                book = book
                            )
                        }
                    }
                )
            }
        }
    }
}


@Composable
private fun BookShelfItem(
    index: Int,
    book: BookUiModel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
    ) {
        BookShelfComponent(
            index = index,
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .align(Alignment.BottomCenter)
        )

        if (book.itemId > 0) {
            BookCover(
                book = book,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun BookShelfComponent(
    index: Int,
    modifier: Modifier = Modifier
) {
    when (index % 3) {
        0 -> BookShelfLeft(modifier = modifier)
        1 -> BookShelfBase(modifier = modifier)
        2 -> BookShelfRight(modifier = modifier)
    }
}
