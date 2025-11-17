package com.example.plugd.ui.screens.community

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
    var replyTo by remember { mutableStateOf<Message?>(null) }
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Launcher for picking images
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
                        navController = navController,
                        onReact = { emoji ->
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

            // Reply preview bar appears when replying
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
                        coroutineScope.launch {
                            viewModel.sendMessage(channelId, messageText, replyTo)
                            messageText = ""
                            replyTo = null
                        }
                    }
                },
                onAttachClick = { attachmentLauncher.launch("image/*") }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatMessageItem(
    message: Message,
    isCurrentUser: Boolean,
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
        if (isCurrentUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = rememberAsyncImagePainter(message.senderProfileUrl ?: ""),
                contentDescription = "My profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
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

                    if (!message.mediaUrl.isNullOrBlank()) {
                        // show image / attachment
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
                        Text(
                            text = message.content,
                            color = textColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
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

            val menuBackground = Color.White

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier,
                containerColor = menuBackground
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 1.dp)
                ) {
                    // Emojis row
                    Row(
                        modifier = Modifier
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
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

                    DropdownMenuItem(
                        text = {
                            Text(
                                "Reply",
                                color = Color(0xFF5D4037),
                                fontWeight = FontWeight.Medium
                            )
                        },
                        onClick = {
                            onReply(message)
                            menuExpanded = false
                        }
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

        // Send button
        IconButton(onClick = onSendClick, enabled = messageText.isNotBlank()) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = if (messageText.isNotBlank()) Color(0xFFFF9800) else Color.Gray
            )
        }
    }
}