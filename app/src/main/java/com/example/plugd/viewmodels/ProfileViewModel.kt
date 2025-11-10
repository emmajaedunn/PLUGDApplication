package com.example.plugd.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.data.repository.EventRepository
import com.example.plugd.data.repository.ProfileRepository
import com.example.plugd.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isOwnProfile = MutableStateFlow(false)
    val isOwnProfile: StateFlow<Boolean> = _isOwnProfile.asStateFlow()

    private val _isFollowing = MutableStateFlow(false)
    val isFollowing: StateFlow<Boolean> = _isFollowing.asStateFlow()

    private val _targetUserId = MutableStateFlow<String?>(null)

    private val allEvents: Flow<List<EventEntity>> = eventRepository.events

    val userEvents: StateFlow<List<EventEntity>> =
        combine(allEvents, _targetUserId) { events, targetId ->
            val uid = targetId.orEmpty()
            events.filter { e -> (e.ownerUid == uid) || (e.userId == uid) }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun loadProfile(userId: String? = null) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch
                val targetUserId = userId ?: currentUserId
                _targetUserId.value = targetUserId
                _isOwnProfile.value = (targetUserId == currentUserId)

                // Observe the profile for real-time updates
                profileRepository.observeRemoteProfile(targetUserId) { live ->
                    if (live != null) {
                        _profile.value = live
                        _isFollowing.value = live.followers.contains(currentUserId)
                    }
                }

                // Initial fetch for quicker UI response
                val remote = profileRepository.getRemoteProfile(targetUserId)
                if (remote != null) {
                    _profile.value = remote
                    _isFollowing.value = remote.followers.contains(currentUserId)
                }

                refreshEvents()

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load profile"
            } finally {
                _loading.value = false
            }
        }
    }

    fun refreshEvents() {
        viewModelScope.launch {
            try {
                eventRepository.refreshEvents()
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "refreshEvents: ${e.message}")
            }
        }
    }

    fun toggleFollow(targetUserId: String) {
        viewModelScope.launch {
            val currentUserId = auth.currentUser?.uid ?: return@launch
            try {
                val isCurrentlyFollowing = _isFollowing.value

                if (isCurrentlyFollowing) {
                    // Unfollow logic
                    profileRepository.removeFollowing(currentUserId, targetUserId)
                    profileRepository.removeFollower(targetUserId, currentUserId)
                } else {
                    // Fetch the current user's profile to get their username
                    val currentUserProfile = profileRepository.getRemoteProfile(currentUserId)
                    val fromUsername = currentUserProfile?.username ?: "Someone" // Fallback

                    // Follow logic
                    profileRepository.addFollowing(currentUserId, targetUserId)
                    profileRepository.addFollower(targetUserId, currentUserId)
                    profileRepository.addActivity(
                        userId = targetUserId,
                        type = "follow",
                        fromUserId = currentUserId,
                        fromUsername = fromUsername, // Pass the username
                        message = "started following you"
                    )
                }

                // Manually refresh the profile state after the action to guarantee a UI update.
                val updatedProfile = profileRepository.getRemoteProfile(targetUserId)
                if (updatedProfile != null) {
                    _profile.value = updatedProfile
                    _isFollowing.value = updatedProfile.followers.contains(currentUserId)
                }

            } catch (e: Exception) {
                Log.e("ProfileViewModel", "toggleFollow error", e)
                _error.value = "An error occurred."
            }
        }
    }

    suspend fun updateProfileField(field: String, value: String) {
        val userId = auth.currentUser?.uid ?: return
        try {
            profileRepository.updateProfileField(userId, field, value)
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to update profile"
        }
    }

    // --- THIS IS THE NEW FUNCTION ---
    // Called by the EditProfileScreen to save social media links.
    suspend fun updateSocials(socials: Map<String, String>) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                profileRepository.updateSocials(userId, socials)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update social links"
            }
        }
    }

    suspend fun uploadProfilePicture(photoUri: android.net.Uri) {
        val userId = auth.currentUser?.uid ?: return
        try {
            profileRepository.uploadProfilePicture(userId, photoUri)
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to upload profile picture"
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                FirebaseAuth.getInstance().signOut()
                profileRepository.clearLocalCache()
                _profile.value = null
                onComplete()
            } catch (e: Exception) {
                _error.value = e.message ?: "Logout failed"
            }
        }
    }
}























/*package com.example.plugd.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.data.mappers.toUserProfile
import com.example.plugd.data.repository.AuthRepository
import com.example.plugd.data.repository.ProfileRepository
import com.example.plugd.model.UserProfile
import com.example.plugd.remote.api.ApiService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
    // Just Added Api
    private val apiService: ApiService
) : ViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isOwnProfile = MutableStateFlow(false)
    val isOwnProfile: StateFlow<Boolean> = _isOwnProfile

    private val _isFollowing = MutableStateFlow(false)
    val isFollowing: StateFlow<Boolean> = _isFollowing

    // JUST ADDED API
    private val _userEvents = MutableStateFlow<List<EventEntity>>(emptyList())
    val userEvents = _userEvents.asStateFlow()

    private val auth = FirebaseAuth.getInstance()

    /** Load profile: cached first, then Firestore, then live updates */
    fun loadProfile(userId: String? = null) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch
                val targetUserId = userId ?: currentUserId
                _isOwnProfile.value = targetUserId == currentUserId

                val local = profileRepository.getLocalProfile(targetUserId)
                if (local != null) _profile.value = local.toUserProfile()

                val remote = profileRepository.getRemoteProfile(targetUserId)
                if (remote != null) _profile.value = remote

                profileRepository.observeRemoteProfile(targetUserId) { liveUser ->
                    if (liveUser != null) _profile.value = liveUser
                }

                if (targetUserId != currentUserId) {
                    _isFollowing.value = profileRepository.isFollowing(currentUserId, targetUserId)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load profile"
            } finally {
                _loading.value = false
            }
        }
    }

    /** Toggle follow/unfollow with activity feed */
    fun toggleFollow(targetUserId: String) {
        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch
                val isCurrentlyFollowing = _isFollowing.value

                val targetFollowersRef = profileRepository.getUserFollowersCollection(targetUserId)
                val currentFollowingRef = profileRepository.getUserFollowingCollection(currentUserId)

                if (isCurrentlyFollowing) {
                    // Unfollow: remove current user from target user's followers
                    targetFollowersRef.document(currentUserId).delete()
                    // Remove target user from current user's following
                    currentFollowingRef.document(targetUserId).delete()
                    _isFollowing.value = false
                } else {
                    // Follow: add current user to target user's followers
                    targetFollowersRef.document(currentUserId).set(mapOf("followedAt" to System.currentTimeMillis()))
                    // Add target user to current user's following
                    currentFollowingRef.document(targetUserId).set(mapOf("followedAt" to System.currentTimeMillis()))
                    _isFollowing.value = true

                    // Add activity notification
                    profileRepository.addActivity(
                        userId = targetUserId,
                        type = "follow",
                        fromUserId = currentUserId,
                        message = "started following you"
                    )
                }

            } catch (e: Exception) {
                Log.e("ProfileViewModel", "toggleFollow error", e)
            }
        }
    }

    fun updateFollowersLocally(addFollower: Boolean, followerId: String) {
        _profile.value = _profile.value?.let { current ->
            val updatedFollowers = if (addFollower) {
                (current.followers ?: emptyList()) + followerId
            } else {
                current.followers?.filter { it != followerId }
            }
            updatedFollowers?.let { current.copy(followers = it) }
        }
    }

    fun checkIfFollowing(targetUserId: String) {
        viewModelScope.launch {
            val currentUserId = auth.currentUser?.uid ?: return@launch
            _isFollowing.value = profileRepository.isFollowing(currentUserId, targetUserId)
        }
    }

    /** Update profile info */
    fun updateProfileField(field: String, value: String) {
        val userId = auth.currentUser?.uid ?: return
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

    // JUST ADDED API
    // Load events for any user
    fun loadUserEvents(userId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getUserEvents(userId)  // Response<List<EventEntity>>
                if (response.isSuccessful) {
                    _userEvents.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed to load events: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.message ?: "Failed to load events"
            }
        }
    }

    // Add event
    fun addEvent(event: EventEntity, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                apiService.addEvent(event)
                // Refresh events after adding
                loadUserEvents(event.userId)
                onComplete?.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.message ?: "Failed to add event"
            }
        }
    }

    // Delete event
    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            try {
                apiService.deleteEvent(eventId) // make sure your API expects a String ID
                _userEvents.value = _userEvents.value.filterNot { it.eventId == eventId }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateEvent(event: EventEntity) {
        viewModelScope.launch {
            try {
                apiService.updateEvent(event.eventId, event)  // your API should take EventEntity as param
                loadUserEvents(event.userId)   // refresh events list
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.message ?: "Failed to update event"
            }
        }
    }






    //

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.logout()
                onComplete()
            } catch (e: Exception) {
                _error.value = e.message ?: "Logout failed"
            }
        }
    }
}*/