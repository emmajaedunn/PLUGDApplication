package com.example.plugd.data.mappers

import com.example.plugd.data.localRoom.entity.ActivityEntity
import com.example.plugd.model.Activity
import com.example.plugd.remote.api.dto.ActivityDto

class ActivityMappers {

    fun ActivityEntity.toActivity() = Activity(
        type = type,
        fromUserId = fromUserId,
        message = message,
        postId = postId,
        timestamp = timestamp
    )

    fun Activity.toEntity(userId: String) = ActivityEntity(
        activityId = "",
        userId = userId,
        fromUserId = fromUserId,
        message = message,
        postId = postId,
        type = type,
        timestamp = timestamp
    )

    // Convert Entity → DTO
    fun toDto(entity: ActivityEntity): ActivityDto = ActivityDto(
        type = entity.type,
        fromUserId = entity.fromUserId,
        message = entity.message,
        postId = entity.postId,
        timestamp = entity.timestamp
    )

    // Convert DTO → Entity
    fun toEntity(dto: ActivityDto, userId: String): ActivityEntity = ActivityEntity(
        activityId = "", // Firestore / Room can generate
        userId = userId,
        fromUserId = dto.fromUserId,
        message = dto.message,
        postId = dto.postId,
        type = dto.type,
        timestamp = dto.timestamp
    )
}