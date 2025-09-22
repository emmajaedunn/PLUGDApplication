package com.example.plugd.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property on Context for DataStore
val Context.dataStore by preferencesDataStore("settings")

object PreferencesKeys {
    val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
}

suspend fun saveOnboardingCompleted(context: Context) {
    context.dataStore.edit { prefs ->
        prefs[PreferencesKeys.ONBOARDING_COMPLETED] = true
    }
}

fun hasSeenOnboarding(context: Context): Flow<Boolean> =
    context.dataStore.data.map { prefs ->
        prefs[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
    }