package com.example.plugd.ui.screens.nav

import EventRepository
import GoogleAuthUiClient
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.plugd.ui.screens.home.AboutHelpPage
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
import com.example.plugd.remote.api.RetrofitInstance
import com.example.plugd.ui.auth.AuthViewModel
import com.example.plugd.ui.auth.AuthViewModelFactory
import com.example.plugd.ui.screens.community.CommunitySettingsPage
import com.example.plugd.ui.screens.plug.PlugDetailsScreen
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
        eventRemote = EventRemoteDataSource(RetrofitInstance.api)
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

        // --- Register Screen ---
        composable(Routes.REGISTER) {
            RegisterScreen(navController = navController, viewModel = authViewModel)
        }

        // --- Login Screen ---
        composable(Routes.LOGIN) {
            LoginScreen(
                navController = navController,
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
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
                        userId = loggedInUserId  // ✅ Add this line
                    )
                },
                loggedInUserId = loggedInUserId
            )
        }

        /* --- Home Screen ---
        composable(Routes.HOME) {
            MainScreenWithBottomNav(
                navController = navController,
                topBar = { HomeTopBar(navController) },
                content = { padding ->
                    HomeScreen(navController = navController, eventViewModel = eventViewModel)
                },
                loggedInUserId = loggedInUserId
            )
        }*/

        // --- Community Screen ---
        composable(Routes.COMMUNITY) {
            MainScreenWithBottomNav(
                navController = navController,
                topBar = { CommunityTopBar(navController) },
                content = { padding ->
                    CommunityScreen(
                        navController = navController,
                        viewModel = chatViewModel
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
            ChatScreen(channelId = channelId, channelName = channelName, viewModel = chatViewModel, currentUserName = "Emma")
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

            /*SettingsScreen(
                navController = navController,
                profileViewModel = profileViewModel,
                onSignOut = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onDeleteAccount = {
                    // Add delete account logic here
                }
            )
        }*/

        // --- Filter Screen ---
        composable(Routes.FILTER) { FilterScreen(navController = navController) }

        // --- About / Help ---
        composable(Routes.ABOUT_SUPPORT) { AboutHelpPage(navController = navController) }

        // --- Community Settings / Filter ---
        composable(Routes.SETTINGS_COMMUNITY) { CommunitySettingsPage(navController = navController) }
    }
}
























/* working package com.example.plugd.ui.screens.nav

import GoogleAuthUiClient
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.plugd.ui.screens.home.AboutHelpPage
import com.example.plugd.ui.screens.plug.AddPlugScreen
import com.example.plugd.ui.screens.activity.ActivityScreen
import com.example.plugd.ui.screens.profile.ProfileScreen
import com.example.plugd.remote.api.RetrofitInstance
import com.example.plugd.data.remoteFireStore.EventRemoteDataSource
import com.example.plugd.data.remoteFireStore.UserRemoteDataSource
import com.example.plugd.data.repository.EventRepository
import com.example.plugd.viewmodels.factory.EventViewModelFactory
import com.example.plugd.viewmodels.factory.ProfileViewModelFactory
import com.example.plugd.viewmodels.EventViewModel
import com.example.plugd.data.localRoom.database.AppDatabase
import com.example.plugd.data.repository.AuthRepository
import com.example.plugd.data.repository.ChatRepository
import com.example.plugd.data.repository.ProfileRepository
import com.example.plugd.remote.firebase.FirebaseAuthService
import com.example.plugd.ui.auth.AuthViewModel
import com.example.plugd.ui.auth.AuthViewModelFactory
import com.example.plugd.ui.screens.community.ChatScreen
import com.example.plugd.ui.screens.community.CommunityFilterPage
import com.example.plugd.ui.screens.community.CommunityScreen
import com.example.plugd.ui.screens.plug.PlugDetailsScreen
import com.example.plugd.ui.screens.profile.SettingsScreen
import com.example.plugd.viewmodels.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.plugd.viewmodels.ProfileViewModel
import com.example.plugd.viewmodels.factory.ChatViewModelFactory

@Composable
fun AppNavHost(startDestination: String = Routes.REGISTER) {
    val context = LocalContext.current
    val navController = rememberNavController()

    // --- Local DB ---
    val db = AppDatabase.getInstance(context)

    // Logged-in UID
    val loggedInUserId: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // --- Chat Repository ---
    val chatRepository = ChatRepository(chatDao = db.chatDao())

    // --- Chat ViewModel ---
    val chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(chatRepository, loggedInUserId)
    )

    // --- Firebase Remote Data Source ---
    val userRemote = UserRemoteDataSource()

    // --- Profile Repository ---
    val profileRepository = ProfileRepository(
        profileDao = db.userProfileDao()
    )

    // --- Auth Repository ---
    val authRepository = AuthRepository(
        authService = FirebaseAuthService(),
        api = RetrofitInstance.api,
        userDao = db.userDao()
    )

// --- Auth ViewModel ---
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository)
    )

    //--- Profile ViewModel ---
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(profileRepository)
    )

    // Load profile when we have a UID
    LaunchedEffect(key1 = loggedInUserId) {
        if (loggedInUserId.isNotBlank()) {
            profileViewModel.loadProfile()
        }
    }

    // --- Event Repository & ViewModel ---
    val eventRepository = EventRepository(
        eventDao = db.eventDao(),
        eventRemote = EventRemoteDataSource(RetrofitInstance.api)
    )
    val eventViewModel: EventViewModel = viewModel(
        factory = EventViewModelFactory(eventRepository)
    )

    // --- Google SSO Client ---
    val googleAuthClient = remember { GoogleAuthUiClient(context) }

    NavHost(navController = navController, startDestination = startDestination) {

// --- Register Screen ---
        composable(Routes.REGISTER) {
            RegisterScreen(navController = navController, viewModel = authViewModel)
        }

// --- Login Screen ---
        composable(Routes.LOGIN) {
            LoginScreen(
                navController = navController,
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }






        /* working  --- Register Screen ---
        composable(Routes.REGISTER) {
            RegisterScreen(navController = navController)
        }

        // --- Login Screen ---
        composable(Routes.LOGIN) {
            LoginScreen(navController = navController,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }*/

        // --- Home Screen ---
        composable(Routes.HOME) {
            MainScreenWithBottomNav(
                navController = navController,
                topBar = { HomeTopBar(navController) },
                content = { padding ->
                    HomeScreen(navController = navController, eventViewModel = eventViewModel)
                },
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
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onDeleteAccount = {
                    // Implement delete account logic if needed
                }
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
                        viewModel = chatViewModel
                    )
                },
                loggedInUserId = loggedInUserId
            )
        }

        // --- Chat Screen ---
        composable(
            route = "${Routes.CHAT}/{channelId}/{channelName}",
            arguments = listOf(
                navArgument("channelId") { type = NavType.StringType },
                navArgument("channelName") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val channelId = backStackEntry.arguments?.getString("channelId") ?: ""
            val channelName = Uri.decode(backStackEntry.arguments?.getString("channelName") ?: "")

            ChatScreen(
                channelId = channelId,
                channelName = channelName,
                viewModel = chatViewModel,
                currentUserName = "Emma" // pass your current user name here
            )
        }

        // --- Add Plug Screen ---
        composable(Routes.ADD) {
            AddPlugScreen(
                navController = navController,
                eventViewModel = eventViewModel,
                currentUserId = loggedInUserId
            )
        }

        // --- Event details route ---
        composable(
            route = "${Routes.PLUG_DETAILS}/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val events by eventViewModel.events.collectAsState()
            val event = events.find { it.eventId == eventId }
            event?.let {
                PlugDetailsScreen(navController = navController, event = it)
            }
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

        // --- Filter Screen (Home) ---
        composable(Routes.FILTER) { FilterScreen(navController = navController) }

        // --- About / Support ---
        composable(Routes.ABOUT_SUPPORT) { AboutHelpPage(navController = navController) }

        // --- Settings Screen (Community) ---
        composable(Routes.SETTINGS_COMMUNITY) {
            CommunityFilterPage(navController = navController)
        }
    }
}*/




















/*@Composable
fun AppNavHost(startDestination: String = Routes.REGISTER) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    // --- Local DB + Remote DataSources ---
    val db = AppDatabase.getInstance(context)
    val userDao = db.userDao()
    val userRemote = UserRemoteDataSource()
    val profileRepository = ProfileRepository(
        profileDao = db.userProfileDao(),
        remoteFirestore = userRemote
    )

    // --- Auth ViewModel ---
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(FirebaseAuthService(), userDao)
    )

    // --- Profile ViewModel (shared) ---
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(profileRepository)
    )

    // Logged-in uid (may be empty string if not signed in)
    val loggedInUserId: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Load profile when we have a uid (do it in LaunchedEffect to avoid calling during composition)
    LaunchedEffect(key1 = loggedInUserId) {
        if (loggedInUserId.isNotBlank()) {
            profileViewModel.loadProfile(userId = loggedInUserId)
        }
    }

    /* --- Chat repository & ViewModel ---
    // Ensure your AppDatabase defines chatDao() and ChatRepository has a matching constructor
    val firebaseDb = FirebaseDatabase.getInstance("https://plugdapp-default-rtdb.firebaseio.com/")
    val chatDao = db.chatDao() // <-- make sure this is present in AppDatabase
    val chatRepository = ChatRepository(FirebaseFirestore.getInstance())
    val chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(chatRepository, loggedInUserId)
    )*/

    // --- Event ViewModel ---
    val eventDao = db.eventDao()
    val eventRemote = EventRemoteDataSource(RetrofitInstance.api)
    val eventRepository = EventRepository(eventDao, eventRemote)

    val eventViewModel: EventViewModel = viewModel(
        factory = EventViewModelFactory(eventRepository)
    )

    // --- Google SSO Client ---
    val googleAuthClient = remember { GoogleAuthUiClient(context) }

    // observe profile state when needed
    val currentProfile by profileViewModel.profile.collectAsState()

    NavHost(navController = navController, startDestination = startDestination) {

        // --- Register Screen ---
        composable(Routes.REGISTER) {
            RegisterScreen(navController = navController)
        }

        // --- Login Screen ---
        composable(Routes.LOGIN) {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    // after login we navigate to home and let ProfileViewModel pick up current user
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // --- Home Screen ---
        composable(Routes.HOME) {
            MainScreenWithBottomNav(
                navController = navController,
                topBar = { HomeTopBar(navController) },
                content = { padding ->
                    HomeScreen(
                        navController = navController,
                        eventViewModel = eventViewModel
                    )
                },
                loggedInUserId = loggedInUserId
            )
        }

        /* --- Community Screen ---
        composable(Routes.COMMUNITY) {
            MainScreenWithBottomNav(
                navController = navController,
                topBar = { CommunityTopBar(navController) },
                    content = { padding ->
                    CommunityScreen(
                        navController = navController,
                        viewModel = chatViewModel
                    )
                },
                loggedInUserId = loggedInUserId
            )
        }*/

        // --- Add Plug Screen ---
        composable(Routes.ADD) {
            AddPlugScreen(
                navController = navController,
                eventViewModel = eventViewModel,
                currentUserId = loggedInUserId
            )
        }

        // --- Event details route ---
        composable(
            route = "${Routes.PLUG_DETAILS}/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val events by eventViewModel.events.collectAsState()
            val event = events.find { it.eventId == eventId }
            event?.let {
                PlugDetailsScreen(navController = navController, event = it)
            }
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
                topBar = { ProfileTopBar(navController = navController) },
                content = { padding ->
                    ProfileScreen(
                        navController = navController,
                        viewModel = profileViewModel
                    )
                },
                loggedInUserId = loggedInUserId
            )
        }

        // --- Settings Screen ---
        composable(Routes.SETTINGS) {
            // Use the shared profileViewModel already declared above
            val currentProfile by profileViewModel.profile.collectAsState()

            SettingsScreen(
                navController = navController,
                profileViewModel = profileViewModel,
                user = currentProfile as UserEntity?, // passes the current profile as UserEntity?
                onSignOut = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onUpdateProfileField = { field, value ->
                    profileViewModel.updateProfileField(field, value)
                }
            )
        }

        // --- Filter Screen ---
        composable(Routes.FILTER) { FilterScreen(navController = navController) }

        // --- About / Support ---
        composable(Routes.ABOUT_SUPPORT) { AboutHelpPage(navController = navController) }
    }
}




















/* @Composable
fun AppNavHost(startDestination: String = Routes.REGISTER) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    // --- Local DB + Remote DataSources ---
    val db = AppDatabase.getInstance(context)
    val userDao = db.userDao()
    val userRemote = UserRemoteDataSource()
    val userRepository = UserRepository(userDao, userRemote)

    // --- Auth ViewModel ---
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository)
    )

    // --- Profile ViewModel (shared) ---
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(userRepository)
    )

    // Logged-in uid
    val loggedInUserId: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Load profile when we have a uid
    LaunchedEffect(key1 = loggedInUserId) {
        if (loggedInUserId.isNotBlank()) {
            profileViewModel.loadUser(loggedInUserId)
        }
    }

    /* --- Chat repository & ViewModel ---
    val firebaseDb = FirebaseDatabase.getInstance("https://plugdapp-default-rtdb.firebaseio.com/")
    val chatDao = db.chatDao()
    val chatRepository = ChatRepository(FirebaseFirestore.getInstance())
    val chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(chatRepository, loggedInUserId)
    )*/

    // --- Event ViewModel ---
    val eventDao = db.eventDao()
    val eventRemote = EventRemoteDataSource(RetrofitInstance.api)
    val eventRepository = EventRepository(eventDao, eventRemote)
    val eventViewModel: EventViewModel = viewModel(
        factory = EventViewModelFactory(eventRepository)
    )

    // --- Google SSO Client ---
    val googleAuthClient = remember { GoogleAuthUiClient(context) }

    NavHost(navController = navController, startDestination = startDestination) {

        // --- Register Screen ---
        composable(Routes.REGISTER) {
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        // --- Login Screen ---
        composable(Routes.LOGIN) {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel,
                onLoginSuccess = {
                    // Navigate to Home on successful login
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // --- Home Screen ---
        composable(Routes.HOME) {
            MainScreenWithBottomNav(
                navController = navController,
                topBar = { HomeTopBar(navController) },
                content = { padding ->
                    HomeScreen(
                        navController = navController,
                        eventViewModel = eventViewModel
                    )
                },
                loggedInUserId = loggedInUserId
            )
        }

        /* --- Community Screen ---
        composable(Routes.COMMUNITY) {
            MainScreenWithBottomNav(
                navController = navController,
                topBar = { CommunityTopBar(navController) },
                content = { padding ->
                    CommunityScreen(
                        navController = navController,
                        viewModel = chatViewModel
                    )
                },
                loggedInUserId = loggedInUserId
            )
        }*/

        // --- Add Plug Screen ---
        composable(Routes.ADD) {
            AddPlugScreen(
                navController = navController,
                eventViewModel = eventViewModel,
                currentUserId = loggedInUserId
            )
        }

        // --- Event details route ---
        composable(
            route = "${Routes.PLUG_DETAILS}/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val events by eventViewModel.events.collectAsState()
            val event = events.find { it.eventId == eventId }
            event?.let {
                PlugDetailsScreen(navController = navController, event = it)
            }
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
                topBar = { ProfileTopBar(navController = navController) },
                content = { padding ->
                    ProfileScreen(
                        navController = navController,
                        viewModel = profileViewModel
                    )
                },
                loggedInUserId = loggedInUserId
            )
        }

        // --- Settings Screen ---
        composable("settings") {
            val userEntity by profileViewModel.user.collectAsState()
            val userProfile = userEntity?.toUserProfile()
            SettingsScreen(
                navController = navController,
                user = userProfile as UserEntity?,
                onUpdateProfileField = { field, value ->
                    userEntity?.let { current ->
                        val updated = when (field) {
                            "username" -> current.copy(username = value)
                            "email" -> current.copy(email = value)
                            "phone" -> current.copy(phone = value)
                            "location", "address" -> current.copy(location = value)
                            "bio" -> current.copy(bio = value)
                            else -> current
                        }
                        profileViewModel.updateProfile(updated)
                    }
                },

                /*onNotificationsChange = { enabled ->
                    userEntity?.let { current ->
                        profileViewModel.no.updateNotificationType("EVENT_REMINDER")
                    }
                },*/
                onSignOut = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        // --- Filter Screen ---
        composable(Routes.FILTER) {
            FilterScreen(navController = navController) }

        // --- About / Support ---
        composable(Routes.ABOUT_SUPPORT) {
            AboutHelpPage(navController = navController) }

        // Forgot Password
        /*composable(Routes.RESET_PASSWORD) {
            ForgotPassword(navController, viewModel)
        }*/
    }
}















/*package com.example.plugd.ui.screens.nav

import GoogleAuthUiClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.plugd.ui.screens.home.AboutHelpPage
import com.example.plugd.ui.screens.plug.AddPlugScreen
import com.example.plugd.ui.screens.activity.ActivityScreen
import com.example.plugd.ui.screens.profile.ProfileScreen
import com.example.plugd.ui.screens.settings.SettingsScreen
import com.example.plugd.remote.api.RetrofitInstance
import com.example.plugd.data.remoteFireStore.EventRemoteDataSource
import com.example.plugd.data.remoteFireStore.UserRemoteDataSource
import com.example.plugd.data.repository.EventRepository
import com.example.plugd.repository.UserRepository
import com.example.plugd.viewmodels.factory.EventViewModelFactory
import com.example.plugd.viewmodels.factory.ProfileViewModelFactory
import com.example.plugd.viewmodels.EventViewModel
import com.example.plugd.data.localRoom.database.AppDatabase
import com.example.plugd.data.localRoom.entity.UserEntity
import com.example.plugd.data.mappers.toUserProfile
import com.example.plugd.remote.firebase.FirebaseAuthService
import com.example.plugd.ui.auth.AuthViewModel
import com.example.plugd.ui.auth.AuthViewModelFactory
import com.example.plugd.ui.screens.plug.PlugDetailsScreen
import com.google.firebase.auth.FirebaseAuth
import com.example.plugd.repository.ChatRepository
import com.example.plugd.ui.screens.community.CommunityScreen
import com.example.plugd.viewmodels.ProfileViewModel
import com.example.plugd.viewmodels.ChatViewModel
import com.example.plugd.viewmodels.factory.ChatViewModelFactory
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AppNavHost(startDestination: String = Routes.REGISTER) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    // --- Local DB + Remote DataSources ---
    val db = AppDatabase.getInstance(context)
    val userDao = db.userDao()
    val userRemote = UserRemoteDataSource()
    val userRepository = UserRepository(userDao, userRemote)

    // --- Auth ViewModel ---
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(FirebaseAuthService(), userDao)
    )

    // --- Profile ViewModel (shared) ---
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(userRepository)
    )

    // Logged-in uid (may be empty string if not signed in)
    val loggedInUserId: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Load profile when we have a uid (do it in LaunchedEffect to avoid calling during composition)
    LaunchedEffect(key1 = loggedInUserId) {
        if (loggedInUserId.isNotBlank()) {
            profileViewModel.loadUser(loggedInUserId)
        }
    }

    // --- Chat repository & ViewModel ---
    // Ensure your AppDatabase defines chatDao() and ChatRepository has a matching constructor
    val firebaseDb = FirebaseDatabase.getInstance("https://plugdapp-default-rtdb.firebaseio.com/")
    val chatDao = db.chatDao() // <-- make sure this is present in AppDatabase
    val chatRepository = ChatRepository(FirebaseFirestore.getInstance())
    val chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(chatRepository, loggedInUserId)
    )

    // --- Event ViewModel ---
    val eventDao = db.eventDao()
    val eventRemote = EventRemoteDataSource(RetrofitInstance.api)
    val eventRepository = EventRepository(eventDao, eventRemote)

    val eventViewModel: EventViewModel = viewModel(
        factory = EventViewModelFactory(eventRepository)
    )

    // --- Google SSO Client ---
    val googleAuthClient = remember { GoogleAuthUiClient(context) }

    // observe profile state when needed
    val currentProfile by profileViewModel.user.collectAsState()

    NavHost(navController = navController, startDestination = startDestination) {

        // --- Register Screen ---
        composable(Routes.REGISTER) {
            RegisterScreen(navController = navController)
        }

        // --- Login Screen ---
        composable(Routes.LOGIN) {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    // after login we navigate to home and let ProfileViewModel pick up current user
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // --- Home Screen ---
        composable(Routes.HOME) {
            MainScreenWithBottomNav(
                navController = navController,
                topBar = { HomeTopBar(navController) },
                content = { padding ->
                    HomeScreen(
                        navController = navController,
                        eventViewModel = eventViewModel
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
                        viewModel = chatViewModel
                    )
                },
                loggedInUserId = loggedInUserId
            )
        }

        // --- Add Plug Screen ---
        composable(Routes.ADD) {
            AddPlugScreen(
                navController = navController,
                eventViewModel = eventViewModel,
                currentUserId = loggedInUserId
            )
        }

        // --- Event details route ---
        composable(
            route = "${Routes.PLUG_DETAILS}/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val events by eventViewModel.events.collectAsState()
            val event = events.find { it.eventId == eventId }
            event?.let {
                PlugDetailsScreen(navController = navController, event = it)
            }
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
                topBar = { ProfileTopBar(navController = navController) },
                content = { padding ->
                    ProfileScreen(
                        navController = navController,
                        viewModel = profileViewModel
                    )
                },
                loggedInUserId = loggedInUserId
            )
        }

        // --- Settings Screen ---
        composable("settings") {
            val userEntity by profileViewModel.user.collectAsState()
            val userProfile = userEntity?.toUserProfile()
            SettingsScreen(
                navController = navController,
                user = userProfile as UserEntity?,
                onUpdateProfileField = { field, value ->
                    userEntity?.let { current ->
                        val updated = when (field) {
                            "username" -> current.copy(username = value)
                            "email" -> current.copy(email = value)
                            "phone" -> current.copy(phone = value)
                            "location", "address" -> current.copy(location = value)
                            "bio" -> current.copy(bio = value)
                            else -> current
                        }
                        profileViewModel.updateProfile(updated)
                    }
                },

                /*onNotificationsChange = { enabled ->
                    userEntity?.let { current ->
                        profileViewModel.no.updateNotificationType("EVENT_REMINDER")
                    }
                },*/
                onSignOut = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        // --- Filter Screen ---
        composable(Routes.FILTER) { FilterScreen(navController = navController) }

        // --- About / Support ---
        composable(Routes.ABOUT_SUPPORT) { AboutHelpPage(navController = navController) }
    }
}*/