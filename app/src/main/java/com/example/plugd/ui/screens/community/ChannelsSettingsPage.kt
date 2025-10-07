package com.example.plugd.ui.screens.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.R
import com.example.plugd.ui.components.SettingsItem
import com.example.plugd.ui.components.SettingsToggle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelsSettingsPage(navController: NavHostController) {
    // Toggle states
    var channelNotificationsEnabled by remember { mutableStateOf(true) }
    var autoJoinChannels by remember { mutableStateOf(false) }
    var showMutedChannels by remember { mutableStateOf(true) }
    var privateChannelsOnly by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Channel Settings") },
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
                label = "Enable channel notifications",
                checked = channelNotificationsEnabled,
                onCheckedChange = { channelNotificationsEnabled = it }
            )

            SettingsToggle(
                label = "Show muted channels",
                checked = showMutedChannels,
                onCheckedChange = { showMutedChannels = it }
            )

            HorizontalDivider(thickness = 1.dp, color = Color.Gray)

            // --- CHANNEL MANAGEMENT ---
            Text("Channel Management", style = MaterialTheme.typography.titleMedium)

            SettingsToggle(
                label = "Auto-join new channels",
                checked = autoJoinChannels,
                onCheckedChange = { autoJoinChannels = it }
            )

            SettingsToggle(
                label = "Private channels only",
                checked = privateChannelsOnly,
                onCheckedChange = { privateChannelsOnly = it }
            )

            HorizontalDivider(thickness = 1.dp, color = Color.Gray)

            // --- OTHER SETTINGS ---
            Text("Other Settings", style = MaterialTheme.typography.titleMedium)

            SettingsItem(
                label = "Manage Channel Roles",
                value = "Adjust permissions and roles for each channel"
            ) {
                // Placeholder for role management action
            }

            SettingsItem(
                label = "Channel Visibility",
                value = "Control which channels are visible to members"
            ) {
                // Placeholder for visibility settings
            }
        }
    }
}