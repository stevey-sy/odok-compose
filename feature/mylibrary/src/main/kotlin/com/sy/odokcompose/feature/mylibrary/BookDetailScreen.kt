package com.sy.odokcompose.feature.mylibrary

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sy.odokcompose.core.designsystem.OdokTheme
import androidx.compose.runtime.getValue
import com.sy.odokcompose.core.designsystem.OdokColors
import com.sy.odokcompose.feature.mylibrary.components.BookActionButtons
import com.sy.odokcompose.feature.mylibrary.components.BookInfo
import com.sy.odokcompose.feature.mylibrary.components.BookPager
import com.sy.odokcompose.feature.mylibrary.components.BookProgress
import com.sy.odokcompose.feature.mylibrary.components.CommentSection

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

    OdokTheme {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(OdokColors.White)
                    .padding(innerPadding)
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
        }
    }
}