package com.example.plugd.viewmodels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plugd.data.repository.ProfileRepository
import com.example.plugd.viewmodels.ChatViewModel

class ChatViewModelFactory(
    private val profileRepository: ProfileRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(profileRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}