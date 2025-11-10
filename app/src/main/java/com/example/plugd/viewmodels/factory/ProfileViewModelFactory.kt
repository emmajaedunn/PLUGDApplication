package com.example.plugd.viewmodels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plugd.data.repository.EventRepository
import com.example.plugd.data.repository.ProfileRepository
import com.example.plugd.viewmodels.ProfileViewModel

class ProfileViewModelFactory(
    private val profileRepository: ProfileRepository,
    private val eventRepository: EventRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(profileRepository, eventRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
