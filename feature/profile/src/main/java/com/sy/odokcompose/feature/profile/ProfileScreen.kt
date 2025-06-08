package com.sy.odokcompose.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.sy.odokcompose.core.designsystem.OdokColors
import com.sy.odokcompose.core.designsystem.OdokTheme

@Composable
fun ProfileScreen(

) {
    OdokTheme {
        Scaffold (
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ){ innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(OdokColors.White)
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Coming Soon",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}