package com.example.plugd.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.ui.screens.nav.ChangePasswordTopBar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ChangePasswordPage(
    navController: NavHostController,
    onPasswordChanged: () -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val db = FirebaseFirestore.getInstance()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var message by remember { mutableStateOf<String?>(null) }
    var messageColor by remember { mutableStateOf(Color.Red) }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { ChangePasswordTopBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Current password
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Current Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // New password
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm New Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Change Password Button
            Button(
                onClick = {
                    if (newPassword != confirmPassword) {
                        message = "New passwords do not match"
                        messageColor = Color.Red
                        return@Button
                    }

                    if (user != null && user.email != null) {
                        // Re-authenticate user
                        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                        user.reauthenticate(credential)
                            .addOnCompleteListener { reauthTask ->
                                if (reauthTask.isSuccessful) {
                                    // Update Firebase Auth password
                                    user.updatePassword(newPassword)
                                        .addOnCompleteListener { updateTask ->
                                            if (updateTask.isSuccessful) {
                                                // Update Firestore "users" collection
                                                db.collection("users").document(user.uid)
                                                    .update("password", newPassword) // consider hashing
                                                    .addOnSuccessListener {
                                                        message = "Password updated successfully!"
                                                        messageColor = Color.Green
                                                        onPasswordChanged()
                                                    }
                                                    .addOnFailureListener { e ->
                                                        message = "Failed to update Firestore: ${e.message}"
                                                        messageColor = Color.Red
                                                    }
                                            } else {
                                                message = "Failed to update password: ${updateTask.exception?.message}"
                                                messageColor = Color.Red
                                            }
                                        }
                                } else {
                                    message = "Current password is incorrect."
                                    messageColor = Color.Red
                                }
                            }
                    } else {
                        message = "No user logged in."
                        messageColor = Color.Red
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Change Password")
            }

            // Message display
            message?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = it, color = messageColor)
            }
        }
    }
}