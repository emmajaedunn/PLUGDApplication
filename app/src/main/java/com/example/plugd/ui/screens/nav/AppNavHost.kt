package com.example.plugd.ui.screens.nav

import EventRepository
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.ui.screens.auth.LoginScreen
import com.example.plugd.ui.screens.auth.RegisterScreen
import com.example.plugd.ui.screens.home.HomeScreen
import com.example.plugd.ui.screens.home.FilterScreen
import com.example.plugd.ui.screens.plug.AddPlugScreen
import com.example.plugd.ui.screens.activity.ActivityScreen
import com.example.plugd.ui.screens.profile.ProfileScreen
import com.example.plugd.ui.screens.profile.SettingsScreen
import com.example.plugd.ui.screens.community.ChatScreen
import com.example.plugd.ui.screens.community.CommunityScreen
import com.example.plugd.viewmodels.ProfileViewModel
import com.example.plugd.viewmodels.EventViewModel
import com.example.plugd.viewmodels.ChatViewModel
import com.example.plugd.viewmodels.factory.ProfileViewModelFactory
import com.example.plugd.viewmodels.factory.EventViewModelFactory
import com.example.plugd.viewmodels.factory.ChatViewModelFactory
import com.example.plugd.data.localRoom.database.AppDatabase
import com.example.plugd.data.repository.AuthRepository
import com.example.plugd.data.repository.ChatRepository
import com.example.plugd.data.repository.ProfileRepository
import com.example.plugd.remote.firebase.FirebaseAuthService
import com.example.plugd.data.remoteFireStore.EventRemoteDataSource
import com.example.plugd.ui.auth.AuthViewModel
import com.example.plugd.ui.auth.AuthViewModelFactory
import com.example.plugd.ui.auth.GoogleAuthUiClient
import com.example.plugd.ui.screens.auth.ForgotPassword
import com.example.plugd.ui.screens.community.ChannelsSettingsPage
import com.example.plugd.ui.screens.community.CommunitySettingsPage
import com.example.plugd.ui.screens.plug.PlugDetailsScreen
import com.example.plugd.ui.screens.settings.AboutHelpPage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AppNavHost(startDestination: String = Routes.REGISTER) {
    val context = LocalContext.current
    val navController = rememberNavController()

    // --- Local DB ---
    val db = AppDatabase.getInstance(context)
    val loggedInUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // --- Repositories ---
    val profileRepository = ProfileRepository(profileDao = db.userProfileDao())
    val authRepository = AuthRepository(
        authService = FirebaseAuthService(),
        userDao = db.userDao()
    )
    val chatRepository = ChatRepository(chatDao = db.chatDao())
    val eventRepository = EventRepository(
        eventDao = db.eventDao(),
        eventRemote = EventRemoteDataSource(FirebaseFirestore.getInstance())
    )

    // --- ViewModels ---
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository)
    )
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(profileRepository, authRepository)
    )
    val chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(chatRepository, loggedInUserId)
    )
    val eventViewModel: EventViewModel = viewModel(
        factory = EventViewModelFactory(eventRepository)
    )

    // --- Google SSO Client ---
    val googleAuthClient = remember { GoogleAuthUiClient(context) }

    // --- Load profile when user is logged in ---
    LaunchedEffect(key1 = loggedInUserId) {
        if (loggedInUserId.isNotBlank()) {
            profileViewModel.loadProfile()
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.REGISTER) {
            RegisterScreen(
                navController = navController,
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                googleAuthClient = googleAuthClient
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                navController = navController,
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                googleAuthClient = googleAuthClient
            )
        }

        composable(Routes.HOME) {
            MainScreenWithBottomNav(
                navController = navController,
                topBar = { HomeTopBar(navController) },
                content = { padding ->
                    HomeScreen(
                        navController = navController,
                        eventViewModel = eventViewModel,
                        userId = loggedInUserId
                    )
                },
                loggedInUserId = loggedInUserId
            )
        }

        // --- Community Screen ---
        composable(Routes.COMMUNITY) {
            MainScreenWithBottomNav(
                navController = navController,
                topBar = { CommunityTopBar(navController) },
                content = { padding ->
                    CommunityScreen(
                        navController = navController,
                        viewModel = chatViewModel,
                        modifier = Modifier
                    )
                },
                loggedInUserId = loggedInUserId
            )
        }

        // --- Chat Page ---
        composable(
            route = "${Routes.CHAT}/{channelId}/{channelName}",
            arguments = listOf(
                navArgument("channelId") { type = NavType.StringType },
                navArgument("channelName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val channelId = backStackEntry.arguments?.getString("channelId") ?: ""
            val channelName = Uri.decode(backStackEntry.arguments?.getString("channelName") ?: "")
            ChatScreen(navController = navController, channelId = channelId, channelName = channelName, viewModel = chatViewModel)
        }

        // --- Add Plug Screen ---
        composable(Routes.ADD) {
            AddPlugScreen(
                navController = navController,
                eventViewModel = eventViewModel,
                currentUserId = loggedInUserId
            )
        }

        // --- Event details ---
        composable(
            route = "${Routes.PLUG_DETAILS}/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val events by eventViewModel.events.collectAsState()
            val event = events.find { it.eventId == eventId }
            event?.let { PlugDetailsScreen(navController = navController, event = it) }
        }

        // --- Activity Screen ---
        composable(Routes.ACTIVITY) {
            MainScreenWithBottomNav(
                navController = navController,
                topBar = { ActivityTopBar(navController) },
                content = { padding -> ActivityScreen(navController = navController) },
                loggedInUserId = loggedInUserId
            )
        }

        // --- Profile Screen ---
        composable(Routes.PROFILE) {
            MainScreenWithBottomNav(
                navController = navController,
                topBar = { ProfileTopBar(navController) },
                content = { padding ->
                    ProfileScreen(navController = navController, profileViewModel = profileViewModel)
                },
                loggedInUserId = loggedInUserId
            )
        }

        // --- Settings Screen (Profile) ---
        composable(Routes.SETTINGS) {
            SettingsScreen(
                navController = navController,
                profileViewModel = profileViewModel,
                onSignOut = {
                    profileViewModel.logout {
                        navController.navigate(Routes.REGISTER) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true } // clears all previous screens
                            launchSingleTop = true // avoid duplicate REGISTER screen
                        }
                    }
                },
                onDeleteAccount = { /* ... */ }
            )
        }

        composable(Routes.RESET_PASSWORD) {
            ForgotPassword(navController = navController, viewModel = authViewModel)
        }

        // --- Filter Screen ---
        composable(Routes.FILTER) { FilterScreen(navController = navController) }

        // --- About / Help ---
        composable(Routes.ABOUT_SUPPORT) { AboutHelpPage(navController = navController) }

        // --- Community Settings / Filter ---
        composable(Routes.SETTINGS_COMMUNITY) { CommunitySettingsPage(navController = navController) }

        // --- Channels Settings ---
        composable(Routes.SETTINGS_CHANNEL) { ChannelsSettingsPage(navController = navController) }
    }
}
