package com.example.plugd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.ui.screens.nav.AppNavHost
import com.example.plugd.ui.theme.PLUGDTheme
import com.example.plugd.ui.utils.AppLanguageHelper

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppLanguageHelper.applyPersistedLanguage(this)

        setContent {
            // State for dark mode, which can be managed similarly if needed
            var isDarkMode by remember { mutableStateOf(false) }

            PLUGDTheme(darkTheme = isDarkMode) {
                AppNavHost(
                    startDestination = Routes.REGISTER, // Or determine dynamically
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { isDarkMode = it }
                )
            }
        }
    }
}
