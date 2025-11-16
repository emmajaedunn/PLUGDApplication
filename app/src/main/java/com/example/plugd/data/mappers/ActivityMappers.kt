package com.example.plugd.data.mappers

import com.example.plugd.data.localRoom.entity.ActivityEntity
import com.example.plugd.model.Activity
import com.example.plugd.remote.api.dto.ActivityDto

class ActivityMappers {

    // Entity -> Domain
    fun ActivityEntity.toActivity() = Activity(
        type = type,
        fromUserId = fromUserId,
        message = message,
        postId = postId,
        timestamp = timestamp
    )

    // Domain -> Entity
    fun Activity.toEntity(ownerUserId: String) = ActivityEntity(
        id = "",                 // let Firestore/Room overwrite if needed
        ownerUserId = ownerUserId,
        fromUserId = fromUserId,
        message = message,
        postId = postId,
        type = type,
        timestamp = timestamp
        // fromUsername will use its default "" for now
    )

    // Entity -> DTO
    fun toDto(entity: ActivityEntity): ActivityDto = ActivityDto(
        type = entity.type,
        fromUserId = entity.fromUserId,
        message = entity.message,
        postId = entity.postId,
        timestamp = entity.timestamp
    )

    // DTO -> Entity
    fun toEntity(dto: ActivityDto, ownerUserId: String): ActivityEntity = ActivityEntity(
        id = "",                 // Firestore id or generated later
        ownerUserId = ownerUserId,
        fromUserId = dto.fromUserId,
        message = dto.message,
        postId = dto.postId,
        type = dto.type,
        timestamp = dto.timestamp
        // fromUsername again falls back to ""
    )
}