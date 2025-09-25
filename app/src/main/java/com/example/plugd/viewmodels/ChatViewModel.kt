package com.example.plugd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plugd.model.Channel
import com.example.plugd.model.Message
import com.example.plugd.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    val channels: StateFlow<List<Channel>> get() = _channels
    private val _channels = MutableStateFlow(emptyList<Channel>())

    val messages: StateFlow<List<Message>> get() = _messages
    private val _messages = MutableStateFlow(emptyList<Message>())

    fun loadChannels() {
        viewModelScope.launch {
            repository.getChannels().collect { list ->
                _channels.value = list
            }
        }
    }

    fun loadMessages(channelId: String) {
        viewModelScope.launch {
            repository.getMessages(channelId).collect { list ->
                _messages.value = list
            }
        }
    }

    fun sendMessage(channelId: String, message: Message) {
        repository.sendMessage(channelId, message)
    }
}