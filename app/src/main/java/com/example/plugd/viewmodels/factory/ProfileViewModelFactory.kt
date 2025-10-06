package com.example.plugd.viewmodels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plugd.data.repository.AuthRepository
import com.example.plugd.data.repository.ProfileRepository
import com.example.plugd.viewmodels.ProfileViewModel

class ProfileViewModelFactory(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(profileRepository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}