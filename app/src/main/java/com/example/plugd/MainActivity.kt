package com.example.plugd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.plugd.data.hasSeenOnboarding
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.ui.screens.nav.AppNavHost
import com.example.plugd.ui.theme.PLUGDTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Firebase.firestore

        setContent {
            PLUGDTheme {
                // Directly start navigation at RoleSelection or Login
                AppNavHost(startDestination = Routes.REGISTER) // <-- skip onboarding
            }
        }
    }
}