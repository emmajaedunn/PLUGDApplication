package com.example.plugd.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.R
import com.example.plugd.ui.auth.AuthViewModel
import com.example.plugd.ui.auth.GoogleAuthUiClient
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.ui.theme.Telegraf
import com.example.plugd.ui.screens.auth.BiometricLogin
import com.example.plugd.ui.utils.EncryptedPreferencesManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

    val scrollState = rememberScrollState()

    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        authState?.let { result ->
            result.onSuccess {
                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            }
            result.onFailure {
                Toast.makeText(
                    context,
                    "Login failed. Please check your email and password.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.plugd_icon),
            contentDescription = "PLUGD App Icon",
            modifier = Modifier.size(260.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = Telegraf,
                fontWeight = androidx.compose.ui.text.font.FontWeight.W700
            )
        )

        Spacer(modifier = Modifier.height(15.dp))

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

        Spacer(modifier = Modifier.height(50.dp))

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
            Text(
                text = "Login",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = Telegraf,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.W500
                )
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Forgot Password link
        Text(
            text = "Forgot Password?",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = Telegraf,
                fontWeight = androidx.compose.ui.text.font.FontWeight.W400
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                    navController.navigate("reset_password")
                }
        )

        Spacer(modifier = Modifier.height(16.dp))

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

        // --- BIOMETRIC LOGIN BUTTON (IF ENABLED) ---
        val biometricEnabled = remember {
            EncryptedPreferencesManager.isBiometricEnabled(context)
        }
        val (savedEmail, savedPassword) = remember {
            EncryptedPreferencesManager.getCredentials(context)
        }

        if (biometricEnabled && !savedEmail.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            BiometricLogin(
                title = "Login with Biometrics",
                canAuthenticate = true,
                onAuthSuccess = {
                    viewModel.login(savedEmail, savedPassword)
                },
                onAuthFailure = {
                    Toast.makeText(
                        context,
                        "Biometric login cancelled or failed. You can still login with email & password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

        // Error message
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}
