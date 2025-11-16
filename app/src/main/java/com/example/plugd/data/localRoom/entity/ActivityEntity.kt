package com.example.plugd.data.localRoom.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: String = "",
    val ownerUserId: String = "",
    val fromUserId: String = "",
    val fromUsername: String = "",
    val message: String = "",
    val postId: String? = null,
    val type: String = "",
    val timestamp: Long = 0L
)





/*@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val activityId: String = "",
    val userId: String = "",
    val fromUserId: String = "",
    val fromUsername: String = "",
    val message: String = "",
    val postId: String? = null,
    val type: String = "",
    val timestamp: Long = 0L
)*/
