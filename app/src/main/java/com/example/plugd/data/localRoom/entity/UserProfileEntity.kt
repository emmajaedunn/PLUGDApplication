package com.example.plugd.data.localRoom.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey var userId: String = "",
    var phone: String? = null,
    var username: String? = null,
    var name: String? = null,
    var email: String? = null,
    var bio: String? = null,
    var location: String? = null,
    var gender: String? = null,
    var followersCount: Int = 0,
    var notificationsEnabled: Boolean = true,
    var darkModeEnabled: Boolean = false,
    var biometricEnabled: Boolean = false,
    var pushEnabled: Boolean = true
)