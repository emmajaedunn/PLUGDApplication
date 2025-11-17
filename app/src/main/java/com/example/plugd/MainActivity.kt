package com.example.plugd

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.plugd.remote.api.spotify.SpotifyLoginManager
import com.example.plugd.remote.api.spotify.SpotifyTokenManager
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.ui.screens.nav.AppNavHost
import com.example.plugd.ui.theme.PLUGDTheme
import com.example.plugd.ui.utils.AppLanguageHelper
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle Spotify login callback
        handleSpotifyRedirect(intent)

        // Apply saved language
        AppLanguageHelper.applyPersistedLanguage(this)

        // Check if user is logged in
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
        val startDestination = if (isLoggedIn) {
            Routes.HOME
        } else {
            Routes.REGISTER
        }

        setContent {
            var isDarkMode by remember { mutableStateOf(false) }

            PLUGDTheme(darkTheme = isDarkMode) {
                AppNavHost(
                    startDestination = startDestination,
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { isDarkMode = it }
                )
            }
        }
    }

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










/*package com.example.plugd

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

        // Handle Spotify login callback
        handleSpotifyRedirect(intent)

        // Apply saved language
        AppLanguageHelper.applyPersistedLanguage(this)

        // Check if user is logged in
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
        val startDestination = if (isLoggedIn) {
            Routes.HOME
        } else {
            Routes.REGISTER
        }

        setContent {
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

    // Handle Spotify login callback
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
}*/
