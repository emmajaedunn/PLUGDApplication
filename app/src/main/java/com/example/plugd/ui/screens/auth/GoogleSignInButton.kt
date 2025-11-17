package com.example.plugd.ui.screens.auth

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.plugd.R
import com.example.plugd.ui.auth.AuthViewModel
import com.example.plugd.ui.auth.GoogleAuthUiClient
import com.example.plugd.ui.theme.Telegraf
import kotlinx.coroutines.launch

@Composable
fun GoogleSignInButton(
    viewModel: AuthViewModel,
    googleAuthClient: GoogleAuthUiClient,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    TextButton(
        onClick = {
            val activity = context as? Activity
            if (activity == null) {
                Toast.makeText(
                    context,
                    "Google sign-in not available.",
                    Toast.LENGTH_SHORT
                ).show()
                return@TextButton
            }

            // Sign in with Google
            scope.launch {
                try {
                    val result = googleAuthClient.signIn()

                    if (result.isSuccess) {
                        val googleCredential = result.getOrThrow()
                        val idToken = googleCredential.idToken

                        if (!idToken.isNullOrEmpty()) {
                            val user = googleAuthClient.firebaseSignInWithGoogle(idToken)

                            if (user != null) {
                                onSuccess()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Google sign-in failed. Please try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Google ID token is null.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Google sign-in failed. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Google sign-in failed. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = "Google icon",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Continue with Google",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = Telegraf,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.W500
                )
            )
        }
    }
}