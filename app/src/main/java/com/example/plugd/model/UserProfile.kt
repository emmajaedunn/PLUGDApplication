package com.example.plugd.model

data class UserProfile(
    val userId: String = "",
    val uid: String? = "",
    val name: String? = "",
    val events: List<Event> = emptyList(),
    val username: String? = "",
    val email: String? = "",
    val phone: String? = null,
    val role: String? = "User",
    val bio: String? = null,
    val gender: String? = null,
    val location: String? = null,
    val followers: List<String> = emptyList(),
    val followersCount: Int = 0,
    val following: List<String> = emptyList(),
    val followingCount: Int = 0,

    val socials: Map<String, String> = emptyMap(),

    // Profile Picture
    val profileImageUrl: String? = null,
    val profilePictureUrl: String? = null,

    // Social Media Links
    val spotifyUrl: String? = null,
    val appleMusicUrl: String? = null,
    val tiktokUrl: String? = null,
    val instagramUrl: String? = null,

    val spotifyPlaylists: List<SpotifyPlaylistEmbedded> = emptyList(),

    // Settings
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val biometricEnabled: Boolean = false,
    val pushEnabled: Boolean = true
)

// Spotify Playlist
data class SpotifyPlaylistEmbedded(
    val id: String = "",
    val name: String = "",
    val imageUrl: String? = null,
    val ownerName: String? = null,
    val externalUrl: String? = null
)