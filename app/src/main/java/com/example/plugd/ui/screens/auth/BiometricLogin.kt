package com.example.plugd.ui.screens.auth

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.plugd.R

private fun Context.findActivity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

// Check if biometric authentication is available
private fun canUseBiometric(context: Context): Boolean {
    val biometricManager = BiometricManager.from(context)
    val authenticators = BIOMETRIC_STRONG or DEVICE_CREDENTIAL
    return biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
}

// Biometric login button
@Composable
fun BiometricLogin(
    title: String,
    canAuthenticate: Boolean = true,
    onAuthSuccess: () -> Unit,
    onAuthFailure: () -> Unit
) {
    val context = LocalContext.current
    val systemSupportsBiometric = remember { canUseBiometric(context) }

    TextButton(
        onClick = {
            if (!canAuthenticate || !systemSupportsBiometric) {
                Toast.makeText(
                    context,
                    "Biometric authentication not available on this device.",
                    Toast.LENGTH_SHORT
                ).show()
                onAuthFailure()
                return@TextButton
            }

            val activity = context.findActivity()
            if (activity == null) {
                Toast.makeText(
                    context,
                    "Cannot start biometric prompt (no Activity).",
                    Toast.LENGTH_SHORT
                ).show()
                onAuthFailure()
                return@TextButton
            }

            val executor = ContextCompat.getMainExecutor(activity)

            // Biometric prompt
            val prompt = BiometricPrompt(
                activity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        onAuthSuccess()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        onAuthFailure()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        if (errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                            Toast.makeText(context, "Error: $errString", Toast.LENGTH_SHORT).show()
                        }
                        onAuthFailure()
                    }
                }
            )

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle("Use your fingerprint or face to login")
                .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                .build()

            prompt.authenticate(promptInfo)
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_fingerprint),
            contentDescription = "Fingerprint Icon",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title)
    }
}
