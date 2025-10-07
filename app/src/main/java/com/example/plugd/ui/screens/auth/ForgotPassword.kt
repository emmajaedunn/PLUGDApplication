package com.example.plugd.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.plugd.ui.auth.AuthViewModel

@Composable
fun ForgotPassword(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    val isEmailValid = email.contains("@") && email.contains(".")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Reset Your Password",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            singleLine = true,
            isError = email.isNotEmpty() && !isEmailValid,
            modifier = Modifier.fillMaxWidth()
        )

        if (email.isNotEmpty() && !isEmailValid) {
            Text(
                text = "Please enter a valid email.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start).padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.resetPassword(email) { success, error ->
                    if (success) {
                        Toast.makeText(context, "Reset email sent", Toast.LENGTH_SHORT).show()
                        navController.navigate("login") {
                            popUpTo("forgot_password") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, error ?: "Failed to send reset email", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            enabled = isEmailValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Send Reset Link")
        }
    }
}