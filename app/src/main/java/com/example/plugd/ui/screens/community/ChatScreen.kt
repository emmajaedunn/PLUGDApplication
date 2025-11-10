package com.example.plugd.ui.screens.community

import android.content.ContentResolver
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.plugd.R
import com.example.plugd.model.Message
import com.example.plugd.ui.navigation.Routes
import com.example.plugd.ui.screens.nav.ChannelsTopBar
import com.example.plugd.viewmodels.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    navController: NavController,
    channelId: String,
    channelName: String,
    viewModel: ChatViewModel
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUserId = currentUser?.uid
    val currentUserPhoto = currentUser?.photoUrl

    var messageText by remember { mutableStateOf("") }
    var replyTo by remember { mutableStateOf<Message?>(null) } // FIX: State for tracking replies
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope() // FIX: Scope for calling suspend functions
    val context = LocalContext.current

    // FIX: Launcher for picking images
    val attachmentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                viewModel.sendAttachment(channelId, uri, context.contentResolver)
            }
        }
    }

    LaunchedEffect(channelId) {
        viewModel.loadMessages(channelId)
    }

    Scaffold(
        topBar = { ChannelsTopBar(navController = navController, channelName = channelName) },
        containerColor = Color(0xFFFFFFFF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                state = listState,
                reverseLayout = false
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatMessageItem(
                        message = msg,
                        isCurrentUser = msg.senderId == currentUserId,
                        currentUserPhoto = currentUserPhoto,
                        navController = navController,
                        onReact = { emoji ->
                            // FIX: Use coroutineScope to call suspend function
                            coroutineScope.launch {
                                viewModel.toggleReaction(channelId, msg.id, emoji)
                            }
                        },
                        onReply = { m -> replyTo = m } // FIX: This now works
                    )
                }
            }

            // Auto-scroll to bottom
            LaunchedEffect(messages.size) {
                if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
            }

            // FIX: Reply preview bar appears when replying
            if (replyTo != null) {
                ReplyPreview(
                    message = replyTo!!,
                    onCancelReply = { replyTo = null }
                )
            }

            ChatInputBar(
                messageText = messageText,
                onMessageChange = { messageText = it },
                onSendClick = {
                    if (messageText.isNotBlank()) {
                        // FIX: Use coroutineScope and pass replyTo message
                        coroutineScope.launch {
                            viewModel.sendMessage(channelId, messageText, replyTo)
                            messageText = ""
                            replyTo = null // Reset reply state after sending
                        }
                    }
                },
                onAttachClick = { attachmentLauncher.launch("image/*") } // FIX: Launch the picker
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatMessageItem(
    message: Message,
    isCurrentUser: Boolean,
    currentUserPhoto: Uri?,
    navController: NavController,
    onReact: (emoji: String) -> Unit,
    onReply: (Message) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val alignment = if (isCurrentUser) Arrangement.End else Arrangement.Start
    val bubbleColor = if (isCurrentUser) Color(0xFFFF9800) else Color(0xFF161B22)
    val textColor = Color.White
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val emojis = listOf("👍", "🔥", "😂", "❤️", "😮")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = alignment
    ) {
        if (!isCurrentUser) {
            Image(
                painter = rememberAsyncImagePainter(model = message.senderProfileUrl ?: ""),
                contentDescription = "User profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .clickable { navController.navigate(Routes.USER_PROFILE.replace("{userId}", message.senderId)) }
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start) {
            Surface(
                color = bubbleColor,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .combinedClickable(
                        onClick = {},
                        onLongClick = { menuExpanded = true }
                    )
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    if (!isCurrentUser) {
                        Text(
                            text = message.senderName ?: "Unknown",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFFFF9800)),
                            modifier = Modifier.clickable { navController.navigate(Routes.USER_PROFILE.replace("{userId}", message.senderId)) }
                        )
                    }

                    if (message.replyToSnippet != null) {
                        Surface(color = bubbleColor.copy(alpha=0.5f), shape = RoundedCornerShape(8.dp)) {
                            Text(
                                text = "↪ ${message.replyToSnippet}",
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor.copy(alpha=0.8f),
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                    }

                    if (message.mediaUrl != null) {
                        if ((message.mediaType ?: "").startsWith("image/")) {
                            Image(
                                painter = rememberAsyncImagePainter(message.mediaUrl),
                                contentDescription = "attachment",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        } else {
                            Text("📎 Attachment", color = textColor)
                        }
                        if (message.content.isNotBlank()) Spacer(Modifier.height(6.dp))
                    }

                    if (message.content.isNotBlank()) {
                        Text(text = message.content, color = textColor, style = MaterialTheme.typography.bodyMedium)
                    }

                    if (message.reactions.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            message.reactions.toList().sortedByDescending { it.second }.forEach { (emoji, count) ->
                                Surface(color = Color.Black.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp)) {
                                    Text(" $emoji $count ", color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                    }
                }
            }

            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    emojis.forEach { e ->
                        Text(
                            text = e,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            modifier = Modifier.clickable {
                                onReact(e)
                                menuExpanded = false
                            }
                        )
                    }
                }
                Divider()
                DropdownMenuItem(text = { Text("Reply") }, onClick = { onReply(message); menuExpanded = false })
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
                modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.Gray)
            )
        }
    }
}

@Composable
fun ReplyPreview(message: Message, onCancelReply: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Replying to ${message.senderName}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(message.content, maxLines = 1, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        IconButton(onClick = onCancelReply, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Cancel Reply", tint = Color.Gray)
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
            Icon(painter = painterResource(id = R.drawable.btn_attach), contentDescription = "Attach", tint = Color(0xFFFF9800))
        }

        TextField(
            value = messageText,
            onValueChange = onMessageChange,
            placeholder = { Text("Message...", color = Color.Gray) },
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFFFF9800)
            )
        )

        // FIX: Added send button
        IconButton(onClick = onSendClick, enabled = messageText.isNotBlank()) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = if (messageText.isNotBlank()) Color(0xFFFF9800) else Color.Gray
            )
        }
    }
}

























/*package com.example.plugd.ui.screens.community

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import com.example.plugd.ui.navigation.Routes
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
    val currentUserPhoto = currentUser?.photoUrl

    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()

    // Load messages
    LaunchedEffect(channelId) {
        viewModel.loadMessages(channelId)
        viewModel.observeRealtimeMessages(channelId) { }
    }

    Scaffold(
        topBar = { ChannelsTopBar(navController = navController, channelName = channelName) },
        containerColor = Color(0xFFFFFFFF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Messages
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
                        currentUserPhoto = currentUserPhoto,
                        navController = navController,
                        onReact = { emoji ->
                            // toggle reaction
                            val mid = msg.id
                            LaunchedEffect(mid + emoji) {
                                viewModel.toggleReaction(channelId, mid, emoji)
                            }
                        },
                        onReply = { m -> replyTo = m }
                    )
                }
            }
            }

            // Auto-scroll to bottom
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
                onAttachClick = { /* TODO: File upload */ }
            )
        }
    }
}

@Composable
fun ChatMessageItem(
    message: Message,
    isCurrentUser: Boolean,
    currentUserPhoto: Uri?,
    navController: NavController,
    onReact: (emoji: String) -> Unit,
    onReply: (Message) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val alignment = if (isCurrentUser) Arrangement.End else Arrangement.Start
    val bubbleColor = if (isCurrentUser) Color(0xFFFF9800) else Color(0xFF161B22)
    val textColor = Color.White
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val emojis = listOf("👍","🔥","😂","❤️","😮")

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

            // bubble with long-press
            Surface(
                color = bubbleColor,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .combinedClickable(
                        onClick = {}, // regular tap = do nothing
                        onLongClick = { menuExpanded = true }
                    )
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    if (!isCurrentUser) {
                        Text(
                            text = message.senderName ?: "Unknown",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF9800)
                            ),
                            modifier = Modifier.clickable {
                                navController.navigate(Routes.USER_PROFILE.replace("{userId}", message.senderId))
                            }
                        )
                    }

                    // If message is a reply, show small quote snippet
                    if (message.replyToSnippet != null) {
                        Text(
                            text = "↪ ${message.replyToSnippet}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFFC107)
                        )
                        Spacer(Modifier.height(4.dp))
                    }

                    // If attachment
                    if (message.mediaUrl != null) {
                        // naive: show image when mediaType starts with image/
                        if ((message.mediaType ?: "").startsWith("image/")) {
                            Image(
                                painter = rememberAsyncImagePainter(message.mediaUrl),
                                contentDescription = "attachment",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        } else {
                            Text("📎 Attachment", color = textColor)
                        }
                        if (message.content.isNotBlank()) Spacer(Modifier.height(6.dp))
                    }

                    if (message.content.isNotBlank()) {
                        Text(text = message.content, color = textColor, style = MaterialTheme.typography.bodyMedium)
                    }

                    // small reactions row (if any)
                    if (message.reactions.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            message.reactions.toList().sortedByDescending { it.second }.forEach { (emoji, count) ->
                                Surface(
                                    color = Color.Black.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(" $emoji $count ", color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                    }
                }
            }

            // context menu: reactions + reply
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    emojis.forEach { e ->
                        Text(
                            text = e,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clickable {
                                    onReact(e)
                                    menuExpanded = false
                                }
                        )
                    }
                }
                Divider()
                DropdownMenuItem(
                    text = { Text("Reply") },
                    onClick = {
                        onReply(message)
                        menuExpanded = false
                    }
                )
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


/*@Composable
fun ChatMessageItem(
    message: Message,
    isCurrentUser: Boolean,
    currentUserPhoto: Uri?,
    navController: NavController
) {
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
                            text = message.senderName ?: "Unknown",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF9800)
                            ),
                            modifier = Modifier.clickable {
                                navController.navigate(Routes.USER_PROFILE.replace("{userId}", message.senderId))
                            }
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
}*/

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
            Icon(
                painter = painterResource(id = R.drawable.btn_attach),
                contentDescription = "Attach",
                tint = Color(0xFFFF9800)
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
            Icon(
                painter = painterResource(id = R.drawable.btn_send),
                contentDescription = "Send",
                tint = Color(0xFFFF9800)
            )
        }
    }
}























/*package com.example.plugd.ui.screens.community

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
    val currentUserPhoto = currentUser?.photoUrl

    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()

    // Load messages
    LaunchedEffect(channelId) {
        viewModel.loadMessages(channelId)
        viewModel.observeRealtimeMessages(channelId) { }
    }

    Scaffold(
        topBar = { ChannelsTopBar(navController = navController, channelName = channelName) },
        containerColor = Color(0xFFFFFFFF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Messages
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
                        currentUserPhoto = currentUserPhoto,
                        navController = navController
                    )
                }
            }

            // Auto-scroll to bottom
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
                onAttachClick = { /* TODO: File upload */ }
            )
        }
    }
}

@Composable
fun ChatMessageItem(
    message: Message,
    isCurrentUser: Boolean,
    currentUserPhoto: Uri?,
    navController: NavController
) {
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
                            text = message.senderName ?: "Unknown",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF9800)
                            ),
                            modifier = Modifier.clickable {
                                navController.navigate("userProfile/${message.senderId}")
                            }
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
            Icon(
                painter = painterResource(id = R.drawable.btn_attach),
                contentDescription = "Attach",
                tint = Color(0xFFFF9800)
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
            Icon(
                painter = painterResource(id = R.drawable.btn_send),
                contentDescription = "Send",
                tint = Color(0xFFFF9800)
            )
        }
    }
}*/





















/* WORKING    package com.example.plugd.ui.screens.community

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
}*/