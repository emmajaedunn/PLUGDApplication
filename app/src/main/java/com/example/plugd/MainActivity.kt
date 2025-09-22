package com.example.plugd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.plugd.data.hasSeenOnboarding
import com.example.plugd.data.saveOnboardingCompleted
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.ui.navigation.SetupNavGraph
import com.example.plugd.ui.screens.onboarding.OnboardingScreen
import com.example.plugd.ui.theme.PLUGDTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PLUGDTheme {
                val context = LocalContext.current
                val navController = rememberNavController()
                val seenOnboarding by hasSeenOnboarding(context).collectAsState(initial = false)

                if (seenOnboarding) {
                    // User has already completed onboarding
                    SetupNavGraph(navController)
                } else {
                    // Show onboarding first
                    OnboardingScreen(navController = navController, onFinish = {
                        // Save completion and navigate to login
                        CoroutineScope(Dispatchers.IO).launch {
                            saveOnboardingCompleted(context)
                        }
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    })
                }
            }
        }
    }
}