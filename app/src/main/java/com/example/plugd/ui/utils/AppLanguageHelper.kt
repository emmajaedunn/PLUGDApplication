package com.example.plugd.ui.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

// 1. Initialize the DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object AppLanguageHelper {

    private val LANGUAGE_KEY = stringPreferencesKey("language_code")

    // 2. Function to set and persist the new language
    fun setAppLocale(context: Context, languageCode: String) {
        // Save the language to DataStore
        runBlocking { // Using runBlocking for simplicity, but a coroutine scope is better
            context.dataStore.edit {
                it[LANGUAGE_KEY] = languageCode
            }
        }

        // Apply the new locale to the app
        val localeList = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    // 3. Function to apply the saved language on app startup
    fun applyPersistedLanguage(context: Context) {
        val currentLanguage = runBlocking { // Block to get language before UI loads
            context.dataStore.data.map { it[LANGUAGE_KEY] ?: "en" }.first()
        }

        val localeList = LocaleListCompat.forLanguageTags(currentLanguage)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
    
    // 4. (Optional) Function to get the current language code as a Flow
    fun getLanguageFlow(context: Context) = context.dataStore.data.map {
        it[LANGUAGE_KEY] ?: "en"
    }
}
