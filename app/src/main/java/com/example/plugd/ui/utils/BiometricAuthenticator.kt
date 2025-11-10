package com.example.plugd.ui.utils

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

// --- THE FIX: Helper function to find the Activity from the Context ---
private fun Context.findActivity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

class BiometricAuthenticator(
    private val context: Context,
    private val onAuthSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
    private val onAuthError: (errorCode: Int, errString: CharSequence) -> Unit = { _, _ -> },
    private val onAuthFailure: () -> Unit = {}
) {
    // --- THE FIX: Use the helper function to safely get the activity ---
    private val activity = context.findActivity() ?: throw IllegalStateException("Context must be a FragmentActivity")

    private val executor = ContextCompat.getMainExecutor(context)

    private val biometricPrompt = BiometricPrompt(activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onAuthSuccess(result)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                    Toast.makeText(context, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                }
                onAuthError(errorCode, errString)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onAuthFailure()
            }
        }
    )

    fun authenticate(title: String = "Biometric Login", subtitle: String = "Log in using your biometric credential") {
        if (BiometricAuthenticator.canAuthenticate(context)) {
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                .build()
            biometricPrompt.authenticate(promptInfo)
        } else {
            Toast.makeText(context, "Biometric authentication not available.", Toast.LENGTH_SHORT).show()
            onAuthError(-1, "Biometric authentication not available")
        }
    }

    companion object {
        fun canAuthenticate(context: Context): Boolean {
            val biometricManager = BiometricManager.from(context)
            val authenticators = BIOMETRIC_STRONG or DEVICE_CREDENTIAL
            return biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
        }
    }
}
