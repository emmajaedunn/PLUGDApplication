package com.example.plugd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plugd.data.mappers.toUserProfile
import com.example.plugd.data.repository.ProfileRepository
import com.example.plugd.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /** Load profile: cached first, then Firestore, then live updates */
    fun loadProfile() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            _error.value = "User not logged in"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            try {
                // 1️⃣ Load cached Room profile
                val local = profileRepository.getLocalProfile(userId)
                if (local != null) _profile.value = local.toUserProfile()

                // 2️⃣ Fetch latest from Firestore once
                val remote = profileRepository.getRemoteProfile(userId)
                if (remote != null) _profile.value = remote

                // 3️⃣ Listen for live updates
                profileRepository.observeRemoteProfile(userId) { liveUser ->
                    if (liveUser != null) _profile.value = liveUser
                }

                if (_profile.value == null) _error.value = "Profile not found"
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load profile"
            } finally {
                _loading.value = false
            }
        }
    }

    /** Update single field */
    fun updateProfileField(field: String, value: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                profileRepository.updateProfileField(userId, field, value)
                _profile.value = _profile.value?.let { current ->
                    when (field) {
                        "username" -> current.copy(username = value)
                        "email" -> current.copy(email = value)
                        "bio" -> current.copy(bio = value)
                        "location" -> current.copy(location = value)
                        else -> current
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update profile"
            }
        }
    }
}
















/*class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    suspend fun loadProfile(uid: String) {
        val local = repository.getLocalProfile(uid)
        if (local != null) {
            _profile.value = local.toUserProfile()
        } else {
            val remote = repository.fetchRemoteProfile(uid)
            _profile.value = remote?.toUserProfile()
        }


   /* fun loadProfile(userId: String?) {
        if (userId == null) {
            _error.value = "No logged-in user"
            return
        }*/

        viewModelScope.launch {
            _loading.value = true
            try {
                // 1. Load from Room
                val local = profileRepository.getLocalProfile(userId)
                if (local != null) _profile.value = local.toUserProfile()

                // 2. Load from API (overrides if newer)
                val remote = profileRepository.fetchRemoteProfile(userId)
                if (remote != null) _profile.value = remote.toUserProfile()

                // 3. Optional: listen to live sync (Firestore/WebSocket)
                profileRepository.observeRemoteProfile(userId)

                if (_profile.value == null) {
                    _error.value = "Profile not found"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load profile"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateProfileField(field: String, value: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                profileRepository.updateProfileField(userId, field, value)

                // Also update the in-memory UI state immediately
                val current = _profile.value
                if (current != null) {
                    val updated = when (field) {
                        "username" -> current.copy(username = value)
                        "email" -> current.copy(email = value)
                        "bio" -> current.copy(bio = value)
                        "location" -> current.copy(location = value)
                        else -> current
                    }
                    _profile.value = updated
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update profile"
            }
        }
    }
}








/*package com.example.plugd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plugd.data.localRoom.entity.UserEntity
import com.example.plugd.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun updateProfile(user: UserEntity) {
        viewModelScope.launch {
            try {
                userRepository.updateUserProfile(user) // updates Room + Firestore
                _user.value = user
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update profile"
            }
        }
    }

    fun loadUser(userId: String?) {
        if (userId == null) {
            _error.value = "No logged-in user"
            _loading.value = false
            return
        }

        viewModelScope.launch {
            _loading.value = true
            try {
                val remoteUser = userRepository.fetchUserFromRemote(userId)
                if (remoteUser != null) {
                    _user.value = remoteUser
                } else {
                    val localUser = userRepository.getUserById(userId)
                    if (localUser != null) {
                        _user.value = localUser
                    } else {
                        _error.value = "User not found"
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load user"
            }
            _loading.value = false
        }
    }

    fun updateNotificationType(type: String, enabled: Boolean) {
        viewModelScope.launch {
            val current = _user.value ?: return@launch
            val updated = current.copy(notificationType = type) // store type
            userRepository.updateUserProfile(updated)
            _user.value = updated
        }
    }
}*/