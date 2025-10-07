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
fun RegisterScreen(
    navController: NavHostController,
    viewModel: AuthViewModel,
    googleAuthClient: GoogleAuthUiClient,
    onRegisterSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
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
                Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                onRegisterSuccess()
            }
            result.onFailure { e ->
                Toast.makeText(context, e.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Observe auth state
    LaunchedEffect(authState) {
        authState?.let { result ->
            result.onSuccess { onRegisterSuccess() }
            result.onFailure { e -> errorMessage = e.message ?: "Registration failed" }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                errorMessage = ""
                when {
                    name.isBlank() || username.isBlank() || email.isBlank() || password.isBlank() -> {
                        errorMessage = "Please fill in all fields"
                    }
                    else -> viewModel.register(name, username, email, password)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Divider
        HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)

        Spacer(modifier = Modifier.height(16.dp))

        // Google Sign-In button
        GoogleSignInButton(
            viewModel = viewModel,
            googleAuthClient = googleAuthClient,
            onSuccess = onRegisterSuccess
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Navigate to Login
        Text(
            text = "Already have an account? Login",
            modifier = Modifier.clickable {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.REGISTER) { inclusive = true }
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