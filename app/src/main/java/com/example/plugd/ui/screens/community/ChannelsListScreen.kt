package com.example.plugd.ui.screens.community

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plugd.viewmodels.ChatViewModel

@Composable
fun ChannelsListScreen(navController: NavHostController, viewModel: ChatViewModel) {
    val channels by viewModel.channels.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadChannels() }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(channels) { channel ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("chat/${channel.id}/${channel.name}")
                    }
                    .padding(16.dp)
            ) {
                Text(text = channel.name, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}