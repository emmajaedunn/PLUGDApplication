package com.example.plugd.ui.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.encryptedDataStore by preferencesDataStore(name = "encrypted_prefs")

object EncryptedPreferencesManager {

    private fun emailKey(userId: String) = stringPreferencesKey("email_$userId")
    private fun passwordKey(userId: String) = stringPreferencesKey("password_$userId")
    private fun biometricKey(userId: String) = booleanPreferencesKey("biometric_$userId")
    private fun languageKey(userId: String) = stringPreferencesKey("language_$userId")

    // Save credentials for a specific user
    suspend fun saveCredentials(context: Context, userId: String, email: String, password: String) {
        context.encryptedDataStore.edit { prefs ->
            prefs[emailKey(userId)] = email
            prefs[passwordKey(userId)] = password
            prefs[biometricKey(userId)] = true
        }
    }

    // Read credentials for a specific user
    fun getCredentials(context: Context, userId: String): Flow<Pair<String?, String?>> {
        return context.encryptedDataStore.data.map { prefs ->
            prefs[emailKey(userId)] to prefs[passwordKey(userId)]
        }
    }

    // Toggle biometrics for a specific user
    suspend fun setBiometricEnabled(context: Context, userId: String, enabled: Boolean) {
        context.encryptedDataStore.edit { prefs ->
            prefs[biometricKey(userId)] = enabled
        }
    }

    fun isBiometricEnabled(context: Context, userId: String): Flow<Boolean> {
        return context.encryptedDataStore.data.map { prefs ->
            prefs[biometricKey(userId)] ?: false
        }
    }

    // Clear all local data for a specific user
    suspend fun clear(context: Context, userId: String) {
        context.encryptedDataStore.edit { prefs ->
            prefs.remove(emailKey(userId))
            prefs.remove(passwordKey(userId))
            prefs.remove(biometricKey(userId))
            prefs.remove(languageKey(userId))
        }
    }

    // 🗣️ Save language preference
    suspend fun saveLanguagePreference(context: Context, userId: String, language: String) {
        context.encryptedDataStore.edit { prefs ->
            prefs[languageKey(userId)] = language
        }
    }

    // 🗣️ Get language preference
    suspend fun getLanguagePreference(context: Context, userId: String): String {
        val prefs = context.encryptedDataStore.data.first()
        return prefs[languageKey(userId)] ?: "English"
    }

    // Simulated function for your current user (you can integrate your actual logic)
    fun getCurrentUserId(): String = "default_user"
}