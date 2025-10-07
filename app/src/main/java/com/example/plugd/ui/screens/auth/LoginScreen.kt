package com.example.plugd.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.ui.auth.AuthViewModel
import com.example.plugd.ui.auth.GoogleAuthUiClient
import com.example.plugd.ui.navigation.Routes

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel,
    googleAuthClient: GoogleAuthUiClient,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()
    var message by remember { mutableStateOf("") }

    // JUST ADDED
    val context = LocalContext.current

    LaunchedEffect(authState) {
        authState?.let { result ->
            result.onSuccess {
                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            }
            result.onFailure { e ->
                Toast.makeText(context, e.message ?: "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Observe auth state
    LaunchedEffect(authState) {
        authState?.let { result ->
            result.onSuccess { onLoginSuccess() }
            result.onFailure { e -> errorMessage = e.message ?: "Login failed" }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Email input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        // Password input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email login button
        Button(
            onClick = {
                when {
                    email.isBlank() || password.isBlank() -> {
                        errorMessage = "Please fill in all fields"
                    }
                    else -> {
                        errorMessage = "" // Clear previous error
                        viewModel.login(email, password)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Forgot Password link
        Text(
            text = "Forgot Password?",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.End)
                .clickable {
                    navController.navigate("reset_password")
                }
        )

        // Divider
        HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)

        Spacer(modifier = Modifier.height(16.dp))

        // Google Sign-In button
        GoogleSignInButton(
            viewModel = viewModel,
            googleAuthClient = googleAuthClient,
            onSuccess = onLoginSuccess
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Navigate to Register
        Text(
            text = "Don't have an account? Register",
            modifier = Modifier.clickable {
                navController.navigate(Routes.REGISTER) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            },
            color = MaterialTheme.colorScheme.primary
        )

        // Error message
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}
