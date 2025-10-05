package com.example.plugd.ui.screens.community

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import com.example.plugd.model.Message
import com.example.plugd.viewmodels.ChatViewModel
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    channelId: String,
    channelName: String,
    viewModel: ChatViewModel,
    currentUserName: String
) {
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(channelId) {
        viewModel.loadMessages(channelId)
        viewModel.observeRealtimeMessages(channelId) { newMsg ->
            // Optional: show notification
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Welcome to $channelName!", style = MaterialTheme.typography.headlineSmall)

        LazyColumn(
            modifier = Modifier.weight(1f).padding(vertical = 8.dp),
            state = listState
        ) {
            items(messages) { msg ->
                Text("${msg.senderName}: ${msg.content}", modifier = Modifier.padding(4.dp))
            }
        }

        // Auto-scroll
        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message") }
            )
            Button(onClick = {
                if (messageText.isNotBlank()) {
                    viewModel.sendMessage(channelId, messageText, currentUserName)
                    messageText = ""
                }
            }) {
                Text("Send")
            }
        }
    }
}

























/*@Composable
fun ChatScreen(channelId: String, channelName: String, viewModel: ChatViewModel, currentUserName: String) {
    val context = LocalContext.current
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()

    // Load messages & observe
    LaunchedEffect(channelId) {
        viewModel.loadMessages(channelId)
        viewModel.observeRealtimeMessages(channelId) { newMsg ->
            if (newMsg.senderId != viewModel.currentUserId) {
                showNotification(context, newMsg)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(channelName, style = MaterialTheme.typography.headlineSmall)

        LazyColumn(modifier = Modifier.weight(1f).padding(vertical = 8.dp)) {
            items(messages) { msg ->
                Text("${msg.senderName}: ${msg.content}", modifier = Modifier.padding(4.dp))
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message") }
            )
            Button(onClick = {
                if (messageText.isNotBlank()) {
                    viewModel.sendMessage(channelId, messageText, currentUserName)
                    messageText = ""
                }
            }) {
                Text("Send")
            }
        }
    }
}

fun showNotification(context: Context, message: Message) {
    val channelId = "plugd_channel"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, "PLUGD Chat", NotificationManager.IMPORTANCE_DEFAULT)
        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_email) // Replace with your icon
        .setContentTitle("New message from ${message.senderName}")
        .setContentText(message.content)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
}*/