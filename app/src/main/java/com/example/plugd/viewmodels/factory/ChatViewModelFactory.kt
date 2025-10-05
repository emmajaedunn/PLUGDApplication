package com.example.plugd.viewmodels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plugd.data.repository.ChatRepository
import com.example.plugd.viewmodels.ChatViewModel

class ChatViewModelFactory(
    private val repository: ChatRepository,
    private val currentUserId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(repository, currentUserId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}