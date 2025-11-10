package com.example.plugd.data.localRoom.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val activityId: String = "",
    val userId: String = "",      // owner of the activity feed
    val fromUserId: String = "",  // who triggered the activity
    val fromUsername: String = "", // The username of the person who triggered the activity
    val message: String = "",
    val postId: String? = null,
    val type: String = "",
    val timestamp: Long = 0L
)
