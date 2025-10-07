package com.example.plugd.data.mappers

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
}