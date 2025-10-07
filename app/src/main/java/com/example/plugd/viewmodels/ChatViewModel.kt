package com.example.plugd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plugd.data.repository.ChatRepository
import com.example.plugd.model.Channel
import com.example.plugd.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: ChatRepository,
    val currentUserId: String
) : ViewModel() {

    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> get() = _channels

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> get() = _messages

    fun loadChannels() {
        viewModelScope.launch {
            repository.getChannels().collect { _channels.value = it }
        }
    }

    fun loadMessages(channelId: String) {
        viewModelScope.launch {
            repository.getMessages(channelId).collect { msgs ->
                _messages.value = msgs
            }
        }
    }

    fun observeRealtimeMessages(channelId: String, onNewMessage: (Message) -> Unit) {
        viewModelScope.launch {
            repository.observeRealtimeMessages(channelId).collect { msg ->
                if (_messages.value.none { it.id == msg.id }) {
                    _messages.value = _messages.value + msg
                    if (msg.senderId != currentUserId) onNewMessage(msg)
                }
            }
        }
    }

    /** ✅ Send message using Firestore username */
    fun sendMessage(channelId: String, content: String) {
        viewModelScope.launch {
            try {
                // Fetch the username from Firestore (or local cache)
                val username = repository.getUserName(currentUserId)

                // Create message
                val message = Message(
                    id = "",
                    channelId = channelId,
                    senderId = currentUserId,
                    content = content,
                    senderName = username,
                    timestamp = System.currentTimeMillis()
                )

                // Send it
                repository.sendMessage(channelId, message)
            } catch (e: Exception) {
                // Optional: handle error (e.g., show a toast)
                e.printStackTrace()
            }
        }
    }
}