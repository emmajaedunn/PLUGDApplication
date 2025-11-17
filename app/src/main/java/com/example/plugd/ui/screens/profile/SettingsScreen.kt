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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.plugd.ui.utils.EncryptedPreferencesManager
import kotlinx.coroutines.launch
import com.example.plugd.R
import com.example.plugd.ui.theme.Telegraf
import com.example.plugd.ui.utils.AppLanguageHelper

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

    val biometricEnabled = remember { EncryptedPreferencesManager.isBiometricEnabled(context) }
    var biometricToggleState by remember { mutableStateOf(biometricEnabled) }

    var showBiometricPrompt by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var enteredPassword by remember { mutableStateOf("") }

    val langCode by AppLanguageHelper.getLanguageFlow(context).collectAsState(initial = "en")

    val languageLabel = when (langCode) {
        "af" -> "Afrikaans"
        "xh" -> "Xhosa"
        else -> "English"
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
                style = MaterialTheme.typography.titleMedium,
                fontFamily = Telegraf,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )

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
                style = MaterialTheme.typography.titleMedium,
                fontFamily = Telegraf,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )

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
                    if (enabled) {
                        // Ask for password to save credentials
                        showPasswordDialog = true
                    } else {
                        // Turn off biometric & clear stored credentials
                        EncryptedPreferencesManager.setBiometricEnabled(context, false)
                        EncryptedPreferencesManager.clear(context)
                    }
                }
            )

            if (showBiometricPrompt) {
                BiometricLogin(
                    title = stringResource(R.string.enable_biometric_login),
                    canAuthenticate = true,
                    onAuthSuccess = {
                        // now we *know* biometrics worked
                        EncryptedPreferencesManager.setBiometricEnabled(context, true)

                        showBiometricPrompt = false
                        biometricToggleState = true
                        Toast.makeText(context, "Biometric login enabled!", Toast.LENGTH_SHORT).show()
                    },
                    onAuthFailure = {
                        EncryptedPreferencesManager.setBiometricEnabled(context, false)
                        showBiometricPrompt = false
                        biometricToggleState = false
                        Toast.makeText(
                            context,
                            "Biometric authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }

            // Language Preferences
            SettingsItem(
                label = stringResource(R.string.language_preferences),
                value = languageLabel,
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
                        // ✅ Don't clear biometric prefs here
                        // Leave EncryptedPreferencesManager as-is so login can use it

                        onSignOut()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.sign_out),
                    fontWeight = FontWeight.W500
                )
            }

            // Delete Account
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.delete_account),
                    fontWeight = FontWeight.W500)
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
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text(stringResource(R.string.enable_biometric_login)) },
            text = {
                Column {
                    Text("Accept biometrics permissions")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = enteredPassword,
                        onValueChange = { enteredPassword = it },
                        label = { Text(stringResource(R.string.password)) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
            },
            // --- THIS IS THE FIX ---
            containerColor = Color.White,
            tonalElevation = 0.dp,
            confirmButton = {
                TextButton(onClick = {
                    val email = userProfile?.email.orEmpty()
                    if (email.isNotEmpty() && enteredPassword.isNotEmpty()) {
                        // 1) Just save credentials
                        EncryptedPreferencesManager.saveCredentials(context, email, enteredPassword)

                        showPasswordDialog = false
                        showBiometricPrompt = true   // 2) Now show the biometric prompt
                    }
                }) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPasswordDialog = false
                    biometricToggleState = false
                }) {
                    Text(stringResource(R.string.cancel))
                }
            })
    }

    // Edit profile fields (username, email, phone, bio, location)
    if (editingField != null) {
        val field = editingField!!
        AlertDialog(
            onDismissRequest = { editingField = null },
            title = { Text("Edit ${field.replaceFirstChar { it.uppercaseChar() }}",
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = Telegraf,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold) },
            text = {
                OutlinedTextField(
                    value = editBuffer,
                    onValueChange = { editBuffer = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                )
            },
            // --- THIS IS THE FIX for the dialog color ---
            // --- THIS IS THE FIX ---
            containerColor = Color.White,
            tonalElevation = 0.dp,
            // -----------------------------------------
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        profileViewModel.updateProfileField(field, editBuffer)
                    }
                    editingField = null
                }) { Text(stringResource(R.string.save),
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = Telegraf,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Normal) }
            },
            dismissButton = {
                TextButton(onClick = {
                    editingField = null
                }) { Text(stringResource(R.string.cancel),
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = Telegraf,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Normal) }
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
            // Label – same as SettingsToggle label
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = Telegraf,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )

            // Value – same style as SettingsToggle subtitle 👇
            if (value.isNotEmpty()) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = Telegraf,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        TextButton(onClick = onAction) {
            Text(actionText)
        }
    }
}

// This is your helper function at the bottom of the file

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
            Text(label,
                style = MaterialTheme.typography.bodyMedium, fontFamily = Telegraf,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
            if (!subtitle.isNullOrEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = Telegraf,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            // --- THIS IS THE FIX for the toggle color ---
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary, // The track color when ON
                checkedBorderColor = Color.Transparent,

                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f), // The track color when OFF
                uncheckedBorderColor = Color.Transparent
            )
            // -----------------------------------------
        )
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
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = Telegraf,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )
            if (value.isNotEmpty()) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = Telegraf,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}