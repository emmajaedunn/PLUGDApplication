/*package com.example.plugd.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_prefs")

class UserPreferences(context: Context) {
    private val dataStore = context.dataStore

    private val USERNAME_KEY = stringPreferencesKey("username")

    val username: Flow<String?> = dataStore.data.map { it[USERNAME_KEY] }

    suspend fun saveUsername(username: String) {
        dataStore.edit { it[USERNAME_KEY] = username }
    }
}*/