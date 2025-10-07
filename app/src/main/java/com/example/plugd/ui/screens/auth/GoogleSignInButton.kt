package com.example.plugd.ui.screens.auth

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.plugd.R
import com.example.plugd.ui.auth.AuthViewModel
import com.example.plugd.ui.auth.GoogleAuthUiClient
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun GoogleSignInButton(
    viewModel: AuthViewModel,
    googleAuthClient: GoogleAuthUiClient,
    onSuccess: () -> Unit
) {
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable {
                (context as? Activity)?.let { activity ->
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            val googleCredential = googleAuthClient.signIn().getOrThrow()
                            val idToken = googleCredential.idToken
                            if (!idToken.isNullOrEmpty()) {
                                viewModel.loginWithGoogle(
                                    GoogleAuthProvider.getCredential(idToken, null)
                                )
                                onSuccess()
                            } else {
                                errorMessage = "Google ID token is null"
                            }
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "Google Sign-In failed"
                        }
                    }
                }
            }
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_google), // your drawable
            contentDescription = "Google Icon",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Continue with Google",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }

    if (errorMessage.isNotEmpty()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}