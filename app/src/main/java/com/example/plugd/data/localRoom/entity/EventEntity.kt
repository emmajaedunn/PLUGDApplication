package com.example.plugd.data.localRoom.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey
    val eventId: String = "",
    val name: String = "",
    val category: String = "",
    val description: String = "",
    val location: String = "",
    val date: Long = 0L,
    val createdBy: String = "",
    val createdByName: String = "",
    val supportDocs: String? = null
)