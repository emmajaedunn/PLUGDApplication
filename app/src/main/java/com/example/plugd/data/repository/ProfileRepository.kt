package com.example.plugd.data.repository

import android.net.Uri
import android.util.Log
import com.example.plugd.data.localRoom.dao.UserProfileDao
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.data.localRoom.entity.UserProfileEntity
import com.example.plugd.model.SpotifyPlaylistEmbedded
import com.example.plugd.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class ProfileRepository(
    private val profileDao: UserProfileDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val usersCollection = firestore.collection("users")
    private val activitiesCollection = firestore.collection("activities")

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance("gs://plugdapp.firebasestorage.app")

    // Observe remote profile
    fun observeRemoteProfile(userId: String, onProfileChanged: (UserProfile?) -> Unit) {
        usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ProfileRepo", "Listen failed", error)
                    onProfileChanged(null)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val data = snapshot.data ?: emptyMap<String, Any>()

                    // Base mapping
                    val base = snapshot.toObject(UserProfile::class.java) ?: UserProfile()

                    // Force followers / following from raw Firestore data
                    val followers = (data["followers"] as? List<*>)?.filterIsInstance<String>()
                        ?: emptyList()
                    val following = (data["following"] as? List<*>)?.filterIsInstance<String>()
                        ?: emptyList()

                    val fixedProfile = base.copy(
                        userId = base.userId.ifBlank { snapshot.id },
                        followers = followers,
                        following = following,
                        followersCount = followers.size,
                        followingCount = following.size
                    )

                    onProfileChanged(fixedProfile)

                    val entity = UserProfileEntity(
                        userId = fixedProfile.userId,
                        phone = fixedProfile.phone,
                        username = fixedProfile.username,
                        name = fixedProfile.name,
                        email = fixedProfile.email,
                        bio = fixedProfile.bio,
                        location = fixedProfile.location,
                        gender = fixedProfile.gender,
                        socials = Gson().toJson(fixedProfile.socials),
                        profilePictureUrl = fixedProfile.profilePictureUrl,
                        followersCount = fixedProfile.followersCount,
                        followingCount = fixedProfile.followingCount,
                        notificationsEnabled = fixedProfile.notificationsEnabled,
                        darkModeEnabled = fixedProfile.darkModeEnabled,
                        biometricEnabled = fixedProfile.biometricEnabled,
                        pushEnabled = fixedProfile.pushEnabled,
                        spotifyPlaylists = fixedProfile.spotifyPlaylists
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        profileDao.insertProfile(entity)
                    }
                } else {
                    onProfileChanged(null)
                }
            }
    }

    // Get local profile
    suspend fun getLocalProfile(userId: String) = profileDao.getProfileById(userId)

    // Get remote profile
    suspend fun getRemoteProfile(userId: String): UserProfile? {
        val snapshot = usersCollection.document(userId).get().await()
        if (!snapshot.exists()) return null

        val data = snapshot.data ?: emptyMap<String, Any>()
        val base = snapshot.toObject(UserProfile::class.java) ?: UserProfile()

        val followers = (data["followers"] as? List<*>)?.filterIsInstance<String>()
            ?: emptyList()
        val following = (data["following"] as? List<*>)?.filterIsInstance<String>()
            ?: emptyList()

        val fixedProfile = base.copy(
            userId = base.userId.ifBlank { snapshot.id },
            followers = followers,
            following = following,
            followersCount = followers.size,
            followingCount = following.size
        )

        // cache in Room
        val entity = UserProfileEntity(
            userId = fixedProfile.userId,
            phone = fixedProfile.phone,
            username = fixedProfile.username,
            name = fixedProfile.name,
            email = fixedProfile.email,
            bio = fixedProfile.bio,
            location = fixedProfile.location,
            gender = fixedProfile.gender,
            socials = Gson().toJson(fixedProfile.socials),
            profilePictureUrl = fixedProfile.profilePictureUrl,
            followersCount = fixedProfile.followersCount,
            followingCount = fixedProfile.followingCount,
            notificationsEnabled = fixedProfile.notificationsEnabled,
            darkModeEnabled = fixedProfile.darkModeEnabled,
            biometricEnabled = fixedProfile.biometricEnabled,
            pushEnabled = fixedProfile.pushEnabled,
            spotifyPlaylists = fixedProfile.spotifyPlaylists
        )

        CoroutineScope(Dispatchers.IO).launch {
            profileDao.insertProfile(entity)
        }

        return fixedProfile
    }

    // Update profile field
    suspend fun updateProfileField(userId: String, field: String, value: String) {
        usersCollection.document(userId).update(field, value).await()
        profileDao.getProfileById(userId)?.let { local ->
            val updated = when (field) {
                "username" -> local.copy(username = value)
                "email" -> local.copy(email = value)
                "bio" -> local.copy(bio = value)
                "location" -> local.copy(location = value)
                "profilePictureUrl" -> local.copy(profilePictureUrl = value) // <-- THIS LINE IS NOW CORRECT
                else -> local
            }
            profileDao.insertProfile(updated)
        }
    }

    // Update Spotify Playlists
    suspend fun updateSpotifyPlaylists(
        userId: String,
        playlists: List<SpotifyPlaylistEmbedded>
    ) {
        // Save to Firestore
        usersCollection.document(userId)
            .update("spotifyPlaylists", playlists)
            .await()

        // Save to Room
        profileDao.getProfileById(userId)?.let { local ->
            val updated = local.copy(spotifyPlaylists = playlists)
            profileDao.insertProfile(updated)
        }
    }

    // Update socials map
    suspend fun updateSocials(userId: String, socials: Map<String, String>) {
        usersCollection.document(userId).update("socials", socials).await()
        profileDao.getProfileById(userId)?.let { localProfile ->
            val socialsJson = Gson().toJson(socials)
            val updatedProfile = localProfile.copy(socials = socialsJson)
            profileDao.insertProfile(updatedProfile)
        }
    }

    // Get user followers collection
    fun getUserFollowersCollection(userId: String) =
        firestore.collection("users").document(userId).collection("followers")

    // Get user following collection
    fun getUserFollowingCollection(userId: String) =
        firestore.collection("users").document(userId).collection("following")

    // Fetch following users
    suspend fun isFollowing(currentUserId: String, targetUserId: String): Boolean {
        val snapshot = usersCollection.document(currentUserId).get().await()
        val following = snapshot.get("following") as? List<String> ?: emptyList()
        return following.contains(targetUserId)
    }

    // Follow / Unfollow
    suspend fun addFollower(targetUserId: String, currentUserId: String) {
        usersCollection.document(targetUserId)
            .set(mapOf("followers" to FieldValue.arrayUnion(currentUserId)), SetOptions.merge())
            .await()
    }

    suspend fun addFollowing(currentUserId: String, targetUserId: String) {
        usersCollection.document(currentUserId)
            .set(mapOf("following" to FieldValue.arrayUnion(targetUserId)), SetOptions.merge())
            .await()
    }

    // Remove follower
    suspend fun removeFollower(targetUserId: String, followerId: String) {
        getUserFollowersCollection(targetUserId).document(followerId).delete().await()
        usersCollection.document(targetUserId)
            .update("followers", FieldValue.arrayRemove(followerId))
            .await()
    }

    // Remove following
    suspend fun removeFollowing(currentUserId: String, targetUserId: String) {
        getUserFollowingCollection(currentUserId).document(targetUserId).delete().await()

        usersCollection.document(currentUserId)
            .update("following", FieldValue.arrayRemove(targetUserId))
            .await()
    }

    // Update profile picture
    suspend fun uploadProfilePicture(userId: String, photoUri: Uri) {
        try {
            Log.d("ProfileRepository", "Using bucket: ${storage.reference.bucket}")

            val storageRef = storage.reference.child("profile_pictures/$userId.jpg")
            Log.d("ProfileRepository", "Uploading to path: ${storageRef.path}")

            storageRef.putFile(photoUri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()

            usersCollection.document(userId)
                .set(
                    mapOf(
                        "profilePictureUrl" to downloadUrl,
                        "profileImageUrl" to downloadUrl
                    ),
                    SetOptions.merge()
                )
                .await()

            Log.d("ProfileRepository", "Profile picture updated: $downloadUrl")

        } catch (e: Exception) {
            Log.e("ProfileRepository", "uploadProfilePicture failed", e)
            throw e
        }
    }

    suspend fun addActivity(
        userId: String,
        type: String,
        fromUserId: String,
        fromUsername: String,
        message: String,
        postId: String? = null
    ) {
        val activityRef = activitiesCollection
            .document(userId)
            .collection("feed")
            .document()

        val activity = mapOf(
            "type" to type,
            "fromUserId" to fromUserId,
            "fromUsername" to fromUsername,
            "message" to message,
            "postId" to postId,
            "timestamp" to System.currentTimeMillis()
        )

        activityRef.set(activity).await()
    }

    // Called after a user you FOLLOW posts a new plug
    suspend fun addMessageReplyActivity(
        toUserId: String,
        fromUserId: String,
        fromUsername: String,
        chatId: String,
        messagePreview: String
    ) {
        addActivity(
            userId = toUserId,
            type = "reply",
            fromUserId = fromUserId,
            fromUsername = fromUsername,
            message = "replied: \"$messagePreview\"",
            postId = chatId
        )
    }

    // Called after a user you FOLLOW posts a new plug
    suspend fun notifyFollowersAboutNewPlug(
        creatorProfile: UserProfile,
        event: EventEntity
    ) {
        val creatorId = creatorProfile.userId
        val creatorUsername = creatorProfile.username ?: "Someone"

        creatorProfile.followers.forEach { followerId ->
            addActivity(
                userId = followerId,
                type = "new_plug",
                fromUserId = creatorId,
                fromUsername = creatorUsername,
                message = "posted a new plug: \"${event.name}\"",
                postId = event.eventId
            )
        }
    }

    // Delete account
    suspend fun deleteAccount() {val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: return
        val user = auth.currentUser ?: return

        try {
            // Delete Firestore Document
            usersCollection.document(userId).delete().await()

            // Delete Profile Picture from Storage
            try {
                val storageRef = FirebaseStorage.getInstance().reference.child("profile_pictures/$userId.jpg")
                storageRef.delete().await()
            } catch (e: Exception) {
                Log.w("ProfileRepository", "Could not delete profile picture, it may not exist.", e)
            }

            // Delete the User from Firebase Authentication
            user.delete().await()

            // Clear any local data
            clearLocalCache()

        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e("ProfileRepository", "Error deleting account", e)
            throw e
        }
    }

    // Clear local cache
    suspend fun clearLocalCache() = profileDao.clearAllProfiles()
}

