package com.example.plugd.data.localRoom.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val eventId: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String,
    val description: String,
    val location: String,
    val date: Long,
    val createdBy: String,
    val supportDocs: String? = null // nullable to allow empty
)