package com.example.plugd

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.example.plugd.remote.api.spotify.SpotifyLoginManager
import com.example.plugd.remote.api.spotify.SpotifyTokenManager
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.ui.screens.nav.AppNavHost
import com.example.plugd.ui.theme.PLUGDTheme
import com.example.plugd.ui.utils.AppLanguageHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1) Handle possible Spotify redirect
        handleSpotifyRedirect(intent)

        // Apply saved language
        AppLanguageHelper.applyPersistedLanguage(this)

        // ✅ Check if user is logged in
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
        val startDestination = if (isLoggedIn) {
            Routes.HOME         // or Routes.PROFILE, whatever your "main" screen is
        } else {
            Routes.REGISTER
        }

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

    // Called when activity is already running and gets a new intent (Spotify callback)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleSpotifyRedirect(intent)
    }

    private fun handleSpotifyRedirect(intent: Intent?) {
        val data = intent?.data ?: return
        if (data.scheme == "plugd" && data.host == "spotify-callback") {
            val code = data.getQueryParameter("code") ?: return
            val verifier = SpotifyLoginManager.lastCodeVerifier ?: return

            lifecycleScope.launch {
                SpotifyTokenManager.exchangeCodeForToken(code, verifier)
            }
        }
    }
}
