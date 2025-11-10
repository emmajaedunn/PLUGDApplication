package com.example.plugd.data.mappers

import com.example.plugd.data.localRoom.entity.UserProfileEntity
import com.example.plugd.data.localRoom.entity.UserEntity
import com.example.plugd.model.UserProfile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// --- From UserEntity (auth) to UserProfileEntity (Room) ---
fun UserEntity.toUserProfileEntity(): UserProfileEntity {
    return UserProfileEntity(
        userId = this.userId,
        name = this.name,
        username = this.username,
        email = this.email
    )
}

// --- From UserProfile (domain model) to Room Entity ---
fun UserProfile.toUserProfileEntity(): UserProfileEntity {
    val socialsJson = Gson().toJson(this.socials)

    return UserProfileEntity(
        userId = this.userId,
        name = this.name,
        username = this.username,
        email = this.email,
        phone = this.phone,
        bio = this.bio,
        location = this.location,
        gender = this.gender,
        followersCount = this.followersCount,
        profilePictureUrl = this.profilePictureUrl,
        socials = socialsJson,
        followingCount = this.followingCount,   // ✅ add this if your model supports it
        notificationsEnabled = this.notificationsEnabled,
        darkModeEnabled = this.darkModeEnabled,
        biometricEnabled = this.biometricEnabled,
        pushEnabled = this.pushEnabled
    )
}

// --- From Room Entity to Domain Model ---
fun UserProfileEntity.toUserProfile(): UserProfile {
    val socialsMap: Map<String, String> = if (this.socials != null) {
        val type = object : TypeToken<Map<String, String>>() {}.type
        Gson().fromJson(this.socials, type)
    } else {
        emptyMap()
    }
    return UserProfile(
        userId = this.userId,
        name = this.name ?: "",
        username = this.username ?: "",
        email = this.email ?: "",
        phone = this.phone ?: "",
        bio = this.bio ?: "",
        location = this.location ?: "",
        gender = this.gender ?: "",
        socials = socialsMap,
        profilePictureUrl = this.profilePictureUrl,
        followersCount = this.followersCount,
        followingCount = this.followingCount,   // ✅ keep follower/following parity
        notificationsEnabled = this.notificationsEnabled,
        darkModeEnabled = this.darkModeEnabled,
        biometricEnabled = this.biometricEnabled,
        pushEnabled = this.pushEnabled,
        events = emptyList() // optional placeholder until events are loaded
    )
}













/* package com.example.plugd.data.mappers

import com.example.plugd.data.localRoom.entity.UserProfileEntity
import com.example.plugd.data.localRoom.entity.UserEntity
import com.example.plugd.model.UserProfile

fun UserEntity.toUserProfileEntity(): UserProfileEntity {
    return UserProfileEntity(
        userId = this.userId,
        name = this.name,
        username = this.username,
        email = this.email
    )
}

fun UserProfile.toUserProfileEntity(): UserProfileEntity {
    return UserProfileEntity(
        userId = this.userId,
        name = this.name,
        username = this.username,
        email = this.email,
        phone = this.phone,
        bio = this.bio,
        location = this.location,
        gender = this.gender,
        followersCount = this.followersCount,
        notificationsEnabled = this.notificationsEnabled,
        darkModeEnabled = this.darkModeEnabled,
        biometricEnabled = this.biometricEnabled,
        pushEnabled = this.pushEnabled
    )
}

fun UserProfileEntity.toUserProfile(): UserProfile {
    return UserProfile(
        userId = this.userId,
        name = this.name ?: "",
        username = this.username ?: "",
        email = this.email ?: "",
        phone = this.phone ?: "",
        bio = this.bio ?: "",
        location = this.location ?: "",
        gender = this.gender ?: "",
        followersCount = this.followersCount,
        notificationsEnabled = this.notificationsEnabled,
        darkModeEnabled = this.darkModeEnabled,
        biometricEnabled = this.biometricEnabled,
        pushEnabled = this.pushEnabled,
        events = emptyList()
    )
}*/