package com.example.plugd.ui.screens.community

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.R
import com.example.plugd.ui.components.SettingsItem
import com.example.plugd.ui.components.SettingsToggle
import com.example.plugd.ui.theme.Telegraf
import com.example.plugd.ui.utils.NotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunitySettingsPage(navController: NavHostController) {

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val notificationHelper = remember { NotificationHelper(context) }

    // Read master toggle (from main Settings screen)
    val masterNotificationsEnabled = remember {
        notificationHelper.isNotificationsEnabled()
    }

    // Community/channel notification toggle (per your requirement)
    var communityNotificationsEnabled by remember {
        mutableStateOf(notificationHelper.isChannelNotificationsEnabled())
    }

    // Other UI-only toggles
    var showCommunitiesInFeed by remember { mutableStateOf(true) }
    var autoJoinNewCommunities by remember { mutableStateOf(false) }
    var mutedCommunities by remember { mutableStateOf(false) }

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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // --- NOTIFICATIONS ---
            Text("Notifications",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = Telegraf,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)

            // 🔸 THIS controls channel/community message notifications
            SettingsToggle(
                label = "Channel & community messages",
                subtitle = "Push notifications when someone posts or replies",
                checked = communityNotificationsEnabled && masterNotificationsEnabled,
                onCheckedChange = { enabled ->
                    if (!masterNotificationsEnabled && enabled) {
                        // Master is OFF – block and warn
                        Toast.makeText(
                            context,
                            "Turn on notifications in Settings > Account first.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@SettingsToggle
                    }

                    communityNotificationsEnabled = enabled
                    notificationHelper.setChannelNotificationsEnabled(enabled)
                }
            )

            SettingsToggle(
                label = "Mute all communities",
                subtitle = "Turn off sounds/banners, keep notifications silent",
                checked = mutedCommunities,
                onCheckedChange = { mutedCommunities = it }
            )

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            // --- COMMUNITY PREFERENCES ---
            Text("Community Preferences",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = Telegraf,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)

            SettingsToggle(
                label = "Show communities in feed",
                subtitle = "Display posts from your joined communities",
                checked = showCommunitiesInFeed,
                onCheckedChange = { showCommunitiesInFeed = it }
            )

            SettingsToggle(
                label = "Auto-join new communities",
                subtitle = "Automatically join recommended groups",
                checked = autoJoinNewCommunities,
                onCheckedChange = { autoJoinNewCommunities = it }
            )

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            // --- OTHER SETTINGS ---
            Text("Other Settings",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = Telegraf,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)

            SettingsItem(
                label = "Community Privacy",
                value = "Control who sees your activity"
            ) {
                // In future you can navigate to a privacy screen
            }
        }
    }
}