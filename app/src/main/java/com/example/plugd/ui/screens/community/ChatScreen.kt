/*package com.example.plugd.ui.screens.community

import android.app.Notification
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.plugd.viewmodels.ChatViewModel

@Composable
fun ChatScreen(
    channelId: String,
    channelName: String,
    viewModel: ChatViewModel
) {
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    LaunchedEffect(channelId) { viewModel.loadMessages(channelId) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = channelName, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))

        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            items(messages) { msg ->
                Text(text = "${msg.senderName}: ${msg.content}", modifier = Modifier.padding(4.dp))
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (messageText.isNotBlank()) {
                    viewModel.sendMessage(channelId,
                        Notification.MessagingStyle.Message(
                            text = "Hello world",
                            timestamp = System.currentTimeMillis(),
                            sender = "Alice"
                        )
                    )
                    messageText = ""
                }
            }) {
                Text("Send")
            }
        }
    }
}*/