package com.example.plugd.ui.screens.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.plugd.data.hasSeenOnboarding
import com.example.plugd.data.saveOnboardingCompleted
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.ui.screens.auth.LoginScreen
import com.example.plugd.ui.screens.auth.RegisterScreen
import com.example.plugd.ui.screens.home.HomeScreen
import com.example.plugd.ui.screens.onboarding.OnboardingScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AppNavHost() {
    val context = LocalContext.current
    val seenOnboarding by hasSeenOnboarding(context).collectAsState(initial = false)
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (seenOnboarding) Routes.LOGIN else Routes.ONBOARDING
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                navController = navController,
                onFinish = {
                    CoroutineScope(Dispatchers.IO).launch {
                        saveOnboardingCompleted(context)
                    }
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) { LoginScreen(navController) }
        composable(Routes.REGISTER) { RegisterScreen(navController) }

        composable(Routes.HOME) { backStackEntry ->
            HomeScreen(navController, role = "Unknown")
        }
    }
}