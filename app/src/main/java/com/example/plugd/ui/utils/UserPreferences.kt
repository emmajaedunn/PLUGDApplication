package com.example.plugd.ui.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create a new, non-encrypted DataStore for user settings
private val Context.dataStore by preferencesDataStore(name = "user_settings")

object UserPreferencesManager {

    private val LANGUAGE_KEY = stringPreferencesKey("language_preference")

    // Flow to get the current language, defaulting to English ("en")
    fun getLanguage(context: Context): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[LANGUAGE_KEY] ?: "en"
        }
    }

    // Function to save the chosen language
    suspend fun saveLanguage(context: Context, languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageCode
        }
    }
}