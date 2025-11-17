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
import com.example.plugd.ui.components.SettingsToggle
import com.example.plugd.ui.theme.Telegraf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelsSettingsPage(navController: NavHostController) {

    // Toggles for channel settings
    var channelNotificationsEnabled by remember { mutableStateOf(true) }
    var showMutedChannels by remember { mutableStateOf(true) }
    var autoJoinChannels by remember { mutableStateOf(false) }
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
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

            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = Telegraf,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )

            SettingsToggle(
                label = "Enable channel notifications",
                subtitle = "Get alerts when new messages are posted",
                checked = channelNotificationsEnabled,
                onCheckedChange = { channelNotificationsEnabled = it }
            )

            SettingsToggle(
                label = "Show muted channels",
                subtitle = "Keep muted channels visible in your list",
                checked = showMutedChannels,
                onCheckedChange = { showMutedChannels = it }
            )

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            Text(
                text = "Channel Activity",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = Telegraf,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )

            SettingsToggle(
                label = "Auto-join recommended channels",
                subtitle = "Automatically join channels suggested for you",
                checked = autoJoinChannels,
                onCheckedChange = { autoJoinChannels = it }
            )

            SettingsToggle(
                label = "Private channels only",
                subtitle = "Only show & join channels that are private",
                checked = privateChannelsOnly,
                onCheckedChange = { privateChannelsOnly = it }
            )
        }
    }
}