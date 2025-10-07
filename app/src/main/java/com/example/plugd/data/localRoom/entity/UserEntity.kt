package com.example.plugd.data.localRoom.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey var userId: String = "",
    var name: String = "",
    var username: String = "",
    var email: String = "",
    var password: String? = ""
)