package com.example.plugd.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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

    var showDeleteDialog by remember { mutableStateOf(false) }

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
            Text(
                text = stringResource(R.string.account_information),
                //"Account Information",
                style = MaterialTheme.typography.titleMedium)

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
                label = stringResource(R.string.password),
                value = "********",
                actionText = stringResource(R.string.reset)
            ) {
                navController.navigate(Routes.CHANGE_PASSWORD)
            }

            Spacer(modifier = Modifier.height(1.dp))

            // --- ACCOUNT SETTINGS ---
            Text(
                text = stringResource(R.string.account_settings),
                //"Account Settings",
                style = MaterialTheme.typography.titleMedium)

            // Notifications
            SettingsToggle(
                label = stringResource(R.string.notifications),
                subtitle = stringResource(R.string.enable_notifications),
                checked = notificationsEnabled,
                onCheckedChange = { checked ->
                    notificationsEnabled = checked
                    notificationHelper.toggleNotifications(checked)
                }
            )

            // Dark mode
            SettingsToggle(
                label = stringResource(R.string.app_theme),
                subtitle = stringResource(R.string.enable_dark_mode),
                checked = isDarkMode,
                onCheckedChange = { onToggleDarkMode(it) }
            )

            // Biometric login
            SettingsToggle(
                label = stringResource(R.string.biometric_authentication),
                subtitle = stringResource(R.string.enable_biometric),
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
                    title = stringResource(R.string.enable_biometric_login),
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
                label = stringResource(R.string.language_preferences),
                value = stringResource(R.string.language_current_english),
                actionText = stringResource(R.string.language_change_button)
            ) {
                navController.navigate(Routes.CHANGE_LANGUAGE)
            }

            // App Version
            AppItem(
                label = "App Version",
                value = "Version 1.0.0"
            )

            Spacer(modifier = Modifier.height(1.dp))

            // Sign Out
            Button(
                onClick = {
                    scope.launch {
                        EncryptedPreferencesManager.setBiometricEnabled(context, currentUserId, false)
                        EncryptedPreferencesManager.clear(context, currentUserId)

                        onSignOut()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.sign_out))
            }

            // Delete Account
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.delete_account))
            }

            // Add this AlertDialog somewhere inside the main Column of your screen
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text(stringResource(R.string.delete_account_confirm_title)) },
                    text = { Text(stringResource(R.string.delete_account_confirm_text)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                // This will call the function we are about to create
                                profileViewModel.deleteAccount {
                                    navController.navigate(Routes.REGISTER) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                                showDeleteDialog = false
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text(stringResource(R.string.delete_account_confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                )
            }
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
                    // We launch a coroutine to call the suspend function
                    scope.launch {
                        profileViewModel.updateProfileField(field, editBuffer)
                    }
                    editingField = null
                }) { Text(stringResource(R.string.save))}
            },
            dismissButton = {
                TextButton(onClick = { editingField = null }) { Text(stringResource(R.string.cancel)) }
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

@Composable
fun AppItem(
    label: String,
    value: String
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
    }
}