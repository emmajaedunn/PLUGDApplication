package com.example.plugd.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property on Context
private val Context.userPrefsDataStore by preferencesDataStore(name = "user_prefs")

object PreferencesKeys {
    val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
}

// Save onboarding completion
suspend fun Context.saveOnboardingCompleted() {
    userPrefsDataStore.edit { prefs ->
        prefs[PreferencesKeys.ONBOARDING_COMPLETED] = true
    }
}

// Check if onboarding has been completed
fun Context.hasSeenOnboarding(): Flow<Boolean> {
    return userPrefsDataStore.data.map { prefs ->
        prefs[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
    }
}