package com.example.plugd.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.ui.screens.nav.SettingsTopBar
import com.example.plugd.ui.utils.NotificationHelper
import com.example.plugd.viewmodels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.plugd.ui.screens.auth.BiometricLogin
import androidx.lifecycle.lifecycleScope
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.example.plugd.ui.utils.EncryptedPreferencesManager
import kotlinx.coroutines.launch
import com.example.plugd.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel = viewModel(),
    onSignOut: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit

) {
    val userProfile by profileViewModel.profile.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Text(text = stringResource(R.string.language_preferences))

    val notificationHelper = remember { NotificationHelper(context) }
    var notificationsEnabled by remember { mutableStateOf(notificationHelper.isNotificationsEnabled()) }

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    var editingField by remember { mutableStateOf<String?>(null) }
    var editBuffer by remember { mutableStateOf("") }

    val biometricEnabled by EncryptedPreferencesManager.isBiometricEnabled(context, currentUserId)
        .collectAsState(initial = false)
    var biometricToggleState by remember { mutableStateOf(biometricEnabled) }
    var showBiometricPrompt by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var enteredPassword by remember { mutableStateOf("") }

    // Keep toggle in sync with stored preference
    LaunchedEffect(biometricEnabled) {
        biometricToggleState = biometricEnabled
    }

    LaunchedEffect(Unit) {
        profileViewModel.loadProfile()
    }

    Scaffold(
        topBar = { SettingsTopBar(navController = navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // --- ACCOUNT INFO ---
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

            SettingsItem(
                label = "Password",
                value = "********",
                actionText = "Reset"
            ) {
                navController.navigate(Routes.CHANGE_PASSWORD)
            }

            Spacer(modifier = Modifier.height(1.dp))

            // --- ACCOUNT SETTINGS ---
            Text("Account Settings", style = MaterialTheme.typography.titleMedium)

            // Notifications
            SettingsToggle(
                label = "Notifications",
                subtitle = "Enable notifications",
                checked = notificationsEnabled,
                onCheckedChange = { checked ->
                    notificationsEnabled = checked
                    notificationHelper.toggleNotifications(checked)
                }
            )

            // Dark mode
            SettingsToggle(
                label = "App Theme",
                subtitle = "Enable dark mode",
                checked = isDarkMode,
                onCheckedChange = { onToggleDarkMode(it) }
            )

            // Biometric login
            SettingsToggle(
                label = "Biometric Authentication",
                subtitle = "Enable fingerprint or face login",
                checked = biometricToggleState,
                onCheckedChange = { enabled ->
                    biometricToggleState = enabled
                    if (enabled) showPasswordDialog = true
                    else scope.launch {
                        EncryptedPreferencesManager.clear(context, currentUserId)
                        EncryptedPreferencesManager.setBiometricEnabled(context, currentUserId, false)
                    }
                }
            )

            if (showBiometricPrompt) {
                BiometricLogin(
                    title = "Enable Biometric Login",
                    canAuthenticate = true,
                    onAuthSuccess = {
                        scope.launch {
                            val email = userProfile?.email.orEmpty()
                            if (email.isNotEmpty() && enteredPassword.isNotEmpty()) {
                                EncryptedPreferencesManager.saveCredentials(context, currentUserId, email, enteredPassword)
                                EncryptedPreferencesManager.setBiometricEnabled(context, currentUserId, true)
                            }
                        }
                        showBiometricPrompt = false
                        biometricToggleState = true
                        Toast.makeText(context, "Biometric login enabled!", Toast.LENGTH_SHORT).show()
                    },
                    onAuthFailure = {
                        scope.launch {
                            EncryptedPreferencesManager.setBiometricEnabled(context, currentUserId, false)
                        }
                        showBiometricPrompt = false
                        biometricToggleState = false
                        Toast.makeText(context, "Biometric authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // Language Preferences
            SettingsItem(
                label = "Language Preferences",
                value = "English",
                actionText = "Change"
            ) {
                navController.navigate(Routes.CHANGE_LANGUAGE)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Out
            Button(
                onClick = {
                    scope.launch {
                        // First, clear biometric and credentials
                        EncryptedPreferencesManager.setBiometricEnabled(context, currentUserId, false)
                        EncryptedPreferencesManager.clear(context, currentUserId)

                        // Then perform logout
                        profileViewModel.logout {
                            navController.navigate(Routes.REGISTER) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Out")
            }

            // Delete Account
            OutlinedButton(
                onClick = onDeleteAccount,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) { Text("Delete Account") }
        }
    }

    // Edit field dialog
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
                    // --- THIS IS THE FIX ---
                    // We launch a coroutine to call the suspend function
                    scope.launch {
                        profileViewModel.updateProfileField(field, editBuffer)
                    }
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
            .padding(vertical = 6.dp),
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

@Composable
fun SettingsToggle(label: String, subtitle: String? = null, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            if (!subtitle.isNullOrEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}