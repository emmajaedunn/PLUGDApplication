package com.example.plugd.ui.screens.community

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.R
import com.example.plugd.ui.screens.nav.CommunityTopBar
import com.example.plugd.ui.screens.theme.PurpleGrey80
import com.example.plugd.ui.theme.Telegraf
import com.example.plugd.viewmodels.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(navController: NavHostController, viewModel: ChatViewModel, modifier: Modifier = Modifier) {
    val channels by viewModel.channels.collectAsState()

    // Load Firestore data when screen opens
    LaunchedEffect(Unit) { viewModel.loadChannels() }

    Scaffold(
        topBar = { CommunityTopBar(navController = navController) },
        bottomBar = {
            // Bottom nav specific for this screen
            NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
            }
        }
    ) { innerPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = "THE PLUG COMMUNITY",
                    style = MaterialTheme.typography.displayLarge.copy(fontFamily = Telegraf),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "All Communities",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            items(channels) { channel ->
                val imageRes = when (channel.name.lowercase()) {
                    "#collab-plug" -> R.drawable.channel_collab_plug
                    "#event-plug" -> R.drawable.channel_event_plug
                    "#gig-plug" -> R.drawable.channel_gig_plug
                    "#music-plug" -> R.drawable.channel_music_plug
                    "#plug-community" -> R.drawable.channel_plug_community
                    else -> R.drawable.channel_plug_community
                }

                Card(
                    modifier = modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PurpleGrey80),
                    onClick = {
                        navController.navigate(
                            "chat/${channel.id}/${Uri.encode(channel.name)}"
                        )
                    },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = "Channel cover",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentScale = ContentScale.Crop
                        )

                        Column(modifier = Modifier.padding(15.dp)) {
                            Text(
                                text = channel.name,
                                style = MaterialTheme.typography.titleLarge
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Join the community!",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.background,
                            )
                        }
                    }
                }
            }
        }
    }
}

