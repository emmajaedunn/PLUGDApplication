package com.example.plugd.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.plugd.R
import com.example.plugd.viewmodels.ProfileViewModel
import com.example.plugd.ui.auth.BiometricLogin
import com.example.plugd.ui.screens.nav.ProfileTopBar
import com.example.plugd.ui.screens.nav.SettingsTopBar
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel = viewModel(),
    onSignOut: () -> Unit = {},
    onDeleteAccount: () -> Unit = {}
) {
    val userProfile by profileViewModel.profile.collectAsState()
    var editingField by remember { mutableStateOf<String?>(null) }
    var editBuffer by remember { mutableStateOf("") }
    var showBiometricPrompt by remember { mutableStateOf(false) }
    var biometricEnabled by remember { mutableStateOf(userProfile?.biometricEnabled ?: false) }

    // Load profile on enter
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(Unit) {
        profileViewModel.loadProfile()
    }

    Scaffold(
        topBar = {
            SettingsTopBar(navController = navController!!)
        }
    ) {
        padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Account Information", style = MaterialTheme.typography.titleMedium)

            val fields = listOf(
                "username" to userProfile?.username.orEmpty(),
                "email" to userProfile?.email.orEmpty(),
                "phone" to userProfile?.phone.orEmpty(),
                "bio" to userProfile?.bio.orEmpty(),
                "location" to userProfile?.location.orEmpty()
            )

            fields.forEach { (field, value) ->
                SettingsItem(
                    label = field.replaceFirstChar { it.uppercaseChar() },
                    value = value
                ) {
                    editingField = field
                    editBuffer = value
                }
            }

            SettingsItem(label = "Password", value = "********", actionText = "Reset") {
                // Handle password reset via Firebase
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Biometric (Face/Touch ID)")
                Switch(
                    checked = biometricEnabled,
                    onCheckedChange = { requested ->
                        if (requested) showBiometricPrompt = true
                        else {
                            biometricEnabled = false
                            profileViewModel.updateProfileField("biometricEnabled", "false")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Sign Out") }

            OutlinedButton(
                onClick = onDeleteAccount,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) { Text("Delete Account") }
        }
    }

    // Biometric prompt
    if (showBiometricPrompt) {
        BiometricLogin(
            title = "Confirm to enable Biometrics",
            onSuccess = {
                biometricEnabled = true
                profileViewModel.updateProfileField("biometricEnabled", "true")
                showBiometricPrompt = false
            },
            onFailure = {
                biometricEnabled = false
                profileViewModel.updateProfileField("biometricEnabled", "false")
                showBiometricPrompt = false
            }
        )
    }

    // Edit Dialog
    if (editingField != null) {
        val field = editingField!!
        AlertDialog(
            onDismissRequest = { editingField = null },
            title = { Text("Edit ${field.replaceFirstChar { it.uppercaseChar() }}") },
            text = {
                OutlinedTextField(
                    value = editBuffer,
                    onValueChange = { editBuffer = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    profileViewModel.updateProfileField(field, editBuffer)
                    editingField = null
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { editingField = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun SettingsItem(
    label: String,
    value: String,
    actionText: String = "Edit",
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            if (value.isNotEmpty()) {
                Text(value, style = MaterialTheme.typography.bodySmall)
            }
        }
        TextButton(onClick = onAction) { Text(actionText) }
    }
}

