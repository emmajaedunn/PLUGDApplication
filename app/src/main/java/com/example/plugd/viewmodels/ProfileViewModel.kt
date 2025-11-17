package com.example.plugd.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.data.repository.EventRepository
import com.example.plugd.data.repository.ProfileRepository
import com.example.plugd.model.SpotifyPlaylistEmbedded
import com.example.plugd.model.UserProfile
import com.example.plugd.remote.api.spotify.SpotifyPlaylist
import com.example.plugd.remote.api.spotify.SpotifyRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private val spotifyRepo = SpotifyRepository()

    private val _playlists = MutableStateFlow<List<SpotifyPlaylist>>(emptyList())
    val playlists: StateFlow<List<SpotifyPlaylist>> = _playlists.asStateFlow()

    // Only show events belonging to this profile
    val userEvents: StateFlow<List<EventEntity>> =
        combine(allEvents, _targetUserId) { events, targetId ->
            val uid = targetId.orEmpty()
            events.filter { e -> (e.ownerUid == uid) || (e.userId == uid) }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Load profile
    fun loadProfile(userId: String? = null) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val currentUser = auth.currentUser
                android.util.Log.d(
                    "ProfileViewModel",
                    "loadProfile() – currentUserId = ${currentUser?.uid}"
                )

                if (currentUser == null) {
                    _error.value = "Not logged in"
                    _loading.value = false
                    return@launch
                }

                val currentUserId = currentUser.uid
                val targetUserId = userId ?: currentUserId

                android.util.Log.d(
                    "ProfileViewModel",
                    "loadProfile() – targetUserId = $targetUserId"
                )

                _targetUserId.value = targetUserId
                _isOwnProfile.value = (targetUserId == currentUserId)

                // Real-time listener
                profileRepository.observeRemoteProfile(targetUserId) { live ->
                    android.util.Log.d(
                        "ProfileViewModel",
                        "observeRemoteProfile callback for $targetUserId, live = $live"
                    )

                    if (live != null) {
                        _profile.value = live

                        viewModelScope.launch {
                            val following = profileRepository.isFollowing(currentUserId, targetUserId)
                            _isFollowing.value = following
                        }
                    }
                }

                val remote = profileRepository.getRemoteProfile(targetUserId)
                if (remote != null) {
                    _profile.value = remote
                    val following = profileRepository.isFollowing(currentUserId, targetUserId)
                    _isFollowing.value = following
                }

                refreshEvents()

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load profile"
            } finally {
                _loading.value = false
            }
        }
    }

    // Refresh events
    fun refreshEvents() {
        viewModelScope.launch {
            try {
                eventRepository.refreshEvents()
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "refreshEvents: ${e.message}")
            }
        }
    }

    // Following toggle
    fun toggleFollow(targetUserId: String) {
        viewModelScope.launch {
            val currentUserId = auth.currentUser?.uid ?: return@launch
            try {
                val isCurrentlyFollowing = _isFollowing.value

                if (isCurrentlyFollowing) {
                    profileRepository.removeFollowing(currentUserId, targetUserId)
                    profileRepository.removeFollower(targetUserId, currentUserId)
                } else {

                    // Get current user's profile for username
                    val currentUserProfile = profileRepository.getRemoteProfile(currentUserId)
                    val fromUsername = currentUserProfile?.username ?: "Someone"

                    // Get target user's profile for username
                    val targetProfile = profileRepository.getRemoteProfile(targetUserId)
                    val targetUsername = targetProfile?.username ?: ""

                    profileRepository.addFollowing(
                        currentUserId = currentUserId,
                        targetUserId = targetUserId,
                    )

                    profileRepository.addFollower(
                        targetUserId = targetUserId,
                        currentUserId = currentUserId,
                    )

                    // Activity feed entry
                    profileRepository.addActivity(
                        userId = targetUserId,
                        type = "follow",
                        fromUserId = currentUserId,
                        fromUsername = fromUsername,
                        message = "started following you"
                    )
                }

                // Refresh following state
                val nowFollowing = profileRepository.isFollowing(currentUserId, targetUserId)
                _isFollowing.value = nowFollowing

                val updatedProfile = profileRepository.getRemoteProfile(targetUserId)
                if (updatedProfile != null) {
                    _profile.value = updatedProfile
                }

            } catch (e: Exception) {
                Log.e("ProfileViewModel", "toggleFollow error", e)
                _error.value = "An error occurred."
            }
        }
    }

    // Update profile field
    suspend fun updateProfileField(field: String, value: String) {
        val userId = auth.currentUser?.uid ?: return
        try {
            profileRepository.updateProfileField(userId, field, value)
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to update profile"
        }
    }

    // Update socials
    fun updateSocials(socials: Map<String, String>) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                profileRepository.updateSocials(userId, socials)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update social links"
            }
        }
    }

    // Update profile picture
    suspend fun uploadProfilePicture(photoUri: Uri) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _error.value = "No logged-in user while uploading profile picture"
            Log.e("ProfileViewModel", "uploadProfilePicture: userId is null")
            return
        }

        try {
            profileRepository.uploadProfilePicture(userId, photoUri)
            Log.d("ProfileViewModel", "Profile picture upload success")
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Profile picture upload failed", e)
            _error.value = e.message ?: "Failed to upload profile picture"
        }
    }

    // Spotify playlists
    fun loadSpotifyPlaylists() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            try {
                // Fetch from Spotify API
                val remote = spotifyRepo.fetchUserPlaylists()

                // Map to embedded model
                val embedded = remote.map { pl ->
                    SpotifyPlaylistEmbedded(
                        id = pl.id,
                        name = pl.name,
                        imageUrl = pl.images.firstOrNull()?.url,
                        ownerName = pl.owner.display_name ?: pl.owner.id,
                        externalUrl = "https://open.spotify.com/playlist/${pl.id}"
                    )
                }

                // Save to Firestore
                profileRepository.updateSpotifyPlaylists(userId, embedded)

                // Refresh profile from Firestore so UI picks it up
                val refreshed = profileRepository.getRemoteProfile(userId)
                _profile.value = refreshed

            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.message ?: "Failed to sync Spotify"
            }
        }
    }

    // Logout
    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                FirebaseAuth.getInstance().signOut()
                profileRepository.clearLocalCache()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _error.value = e.message ?: "Logout failed"
                }
            }
        }
    }

    // Delete account
    fun deleteAccount(onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                profileRepository.deleteAccount()

                withContext(Dispatchers.Main) {
                    onComplete()
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete account. Please try logging out and in again."
                Log.e("ProfileViewModel", "Delete account failed", e)
            }
        }
    }
}