package com.example.plugd.ui.screens.community

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.plugd.R
import com.example.plugd.model.Message
import com.example.plugd.ui.screens.nav.ChannelsTopBar
import com.example.plugd.viewmodels.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(
    navController: NavController,
    channelId: String,
    channelName: String,
    viewModel: ChatViewModel
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUserId = currentUser?.uid
    val currentUserName = currentUser?.displayName ?: "Unknown"
    val currentUserPhoto = currentUser?.photoUrl

    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(channelId) {
        viewModel.loadMessages(channelId)
        viewModel.observeRealtimeMessages(channelId) { }
    }

    Scaffold(
        topBar = {
            ChannelsTopBar(navController = navController, channelName = channelName)
        },
        containerColor = Color(0xFFFFFFFF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Chat messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                state = listState
            ) {
                items(messages) { msg ->
                    ChatMessageItem(
                        message = msg,
                        isCurrentUser = msg.senderId == currentUserId,
                        currentUserPhoto = currentUserPhoto
                    )
                }
            }

            LaunchedEffect(messages.size) {
                if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
            }

            // Input bar
            ChatInputBar(
                messageText = messageText,
                onMessageChange = { messageText = it },
                onSendClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(channelId, messageText)
                        messageText = ""
                    }
                },
                onAttachClick = {
                    // TODO: Implement file upload (Firebase Storage)
                }
            )
        }
    }
}

@Composable
fun ChatMessageItem(message: Message, isCurrentUser: Boolean, currentUserPhoto: Uri?) {
    val alignment = if (isCurrentUser) Arrangement.End else Arrangement.Start
    val bubbleColor = if (isCurrentUser) Color(0xFFFF9800) else Color(0xFF161B22)
    val textColor = Color.White
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = alignment
    ) {
        if (!isCurrentUser) {
            Image(
                painter = rememberAsyncImagePainter(currentUserPhoto ?: ""),
                contentDescription = "User profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start) {
            Surface(
                color = bubbleColor,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    if (!isCurrentUser) {
                        Text(
                            text = message.senderName,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF9800)
                            )
                        )
                    }
                    Text(
                        text = message.content,
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Text(
                text = timeFormatter.format(Date(message.timestamp)),
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        if (isCurrentUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = rememberAsyncImagePainter(currentUserPhoto ?: ""),
                contentDescription = "My profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        }
    }
}

@Composable
fun ChatInputBar(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onAttachClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF000000))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onAttachClick) {
            Icon(painter = painterResource(id = R.drawable.btn_attach), contentDescription = "Attach", tint = Color(
                0xFFFF9800
            )
            )
        }

        TextField(
            value = messageText,
            onValueChange = onMessageChange,
            placeholder = { Text("Message...", color = Color.Gray) },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0x5B4F4B46),
                unfocusedContainerColor = Color(0x40000000),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        IconButton(onClick = onSendClick) {
            Icon(painter = painterResource(id = R.drawable.btn_send), contentDescription = "Send", tint = Color(
                0xFFFF9800
            )
            )
        }
    }
}

















/* working recent package com.example.plugd.ui.screens.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.plugd.ui.screens.nav.ChannelsTopBar
import com.example.plugd.viewmodels.ChatViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ChatScreen(
    navController: NavController,
    channelId: String,
    channelName: String,
    viewModel: ChatViewModel
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUserName = currentUser?.displayName ?: "Unknown"

    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()

    // Load + listen for messages
    LaunchedEffect(channelId) {
        viewModel.loadMessages(channelId)
        viewModel.observeRealtimeMessages(channelId) { /* optional notifications */ }
    }

    Scaffold(
        topBar = {
            ChannelsTopBar(
                navController = navController,
                channelName = channelName
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                state = listState
            ) {
                items(messages) { msg ->
                    Text(
                        "${msg.senderName}: ${msg.content}",
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            // Auto-scroll to bottom
            LaunchedEffect(messages.size) {
                if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
            }

            // Message input area
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message") }
                )
                Button(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendMessage(channelId, messageText)
                            messageText = ""
                        }
                    }
                ) {
                    Text("Send")
                }
            }
        }
    }
}*/


















/* correct package com.example.plugd.ui.screens.community

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
import com.google.firebase.auth.FirebaseAuth
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

    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUserName = currentUser?.displayName ?: "Unknown"

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
}*/

























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