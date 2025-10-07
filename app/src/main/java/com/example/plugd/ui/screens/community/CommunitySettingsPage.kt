package com.example.plugd.ui.screens.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.plugd.ui.components.SettingsItem
import com.example.plugd.ui.components.SettingsToggle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunitySettingsPage(navController: NavHostController) {
    // Toggle states
    var notificationsEnabled by remember { mutableStateOf(true) }
    var showCommunitiesInFeed by remember { mutableStateOf(true) }
    var autoJoinNewCommunities by remember { mutableStateOf(false) }
    var mutedCommunities by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Community Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.btn_back),
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- NOTIFICATIONS ---
            Text("Notifications", style = MaterialTheme.typography.titleMedium)

            SettingsToggle(
                label = "Enable notifications",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )

            SettingsToggle(
                label = "Show communities in feed",
                checked = showCommunitiesInFeed,
                onCheckedChange = { showCommunitiesInFeed = it }
            )

            SettingsToggle(
                label = "Auto-join new communities",
                checked = autoJoinNewCommunities,
                onCheckedChange = { autoJoinNewCommunities = it }
            )

            SettingsToggle(
                label = "Mute all communities",
                checked = mutedCommunities,
                onCheckedChange = { mutedCommunities = it }
            )

            HorizontalDivider(thickness = 1.dp, color = Color.Gray)

            // --- OTHER SETTINGS ---
            Text("Other Settings", style = MaterialTheme.typography.titleMedium)

            SettingsItem(
                label = "Manage Moderation",
                value = "Report issues, block users, and adjust permissions"
            ) {
                // Placeholder for moderation action
            }

            SettingsItem(
                label = "Community Privacy",
                value = "Adjust who can see your messages and profile"
            ) {
                // Placeholder for discoverability action
            }
        }
    }
}




















/*package com.example.plugd.ui.screens.community

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunitySettingsPage(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* No title */ },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack() // <-- Go back to ProfileScreen
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.btn_back),
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "This is a placeholder for the community settings page.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}*/