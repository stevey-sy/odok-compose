package com.sy.odokcompose.feature.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.sy.odokcompose.core.designsystem.icon.OdokIcons

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: (FirebaseUser) -> Unit,
    onError: (Exception) -> Unit
) {
    val state = viewModel.loginUiState
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let { idToken ->
                viewModel.signIn(idToken)
            }
        } catch (e: Exception) {
            viewModel.handleError(e)
        }
    }

    LaunchedEffect(state) {
        when (state) {
            is LoginUiState.Success -> onLoginSuccess(state.user)
            is LoginUiState.Error -> onError(state.exception)
            else -> {}
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // App Logo
                Image(
                    painter = painterResource(id = OdokIcons.AppLogo),
                    contentDescription = "오독오독 로고",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 48.dp)
                )
                
                when (state) {
                    is LoginUiState.Loading -> {
                        CircularProgressIndicator()
                        Text(
                            text = "로그인 중...",
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                    is LoginUiState.Error -> {
                        Text(
                            text = "로그인 실패: ${state.exception.message}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        GoogleSignInButton(
                            onClick = {
                                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken("745296035891-05cm9tl293ldvkfla3pumtaarr3dhcru.apps.googleusercontent.com")
                                    .requestEmail()
                                    .build()
                                val client = GoogleSignIn.getClient(context, gso)
                                launcher.launch(client.signInIntent)
                            },
                            text = "다시 시도"
                        )
                    }
                    else -> {
                        GoogleSignInButton(
                            onClick = {
                                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(BuildConfig.GOOGLE_LOGIN_API_CLIENT_ID)
                                    .requestEmail()
                                    .build()
                                val client = GoogleSignIn.getClient(context, gso)
                                launcher.launch(client.signInIntent)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GoogleSignInButton(
    onClick: () -> Unit,
    text: String = "Google 계정으로 로그인",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFF747775),
                shape = RoundedCornerShape(4.dp)
            )
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 12.dp, end = 12.dp)
        ) {
            // Google 로고 이미지 사용
            Image(
                painter = painterResource(id = OdokIcons.GLogo),
                contentDescription = "Google 로고",
                modifier = Modifier.size(18.dp)
            )
            
            Spacer(modifier = Modifier.width(10.dp))
            
            Text(
                text = text,
                color = Color(0xFF1F1F1F),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
