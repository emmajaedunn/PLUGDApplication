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

    fun Activity.toEntity(ownerUserId: String) = ActivityEntity(
        id = "",
        ownerUserId = ownerUserId,
        fromUserId = fromUserId,
        message = message,
        postId = postId,
        type = type,
        timestamp = timestamp
    )

    fun toDto(entity: ActivityEntity): ActivityDto = ActivityDto(
        type = entity.type,
        fromUserId = entity.fromUserId,
        message = entity.message,
        postId = entity.postId,
        timestamp = entity.timestamp
    )

    fun toEntity(dto: ActivityDto, ownerUserId: String): ActivityEntity = ActivityEntity(
        id = "",
        ownerUserId = ownerUserId,
        fromUserId = dto.fromUserId,
        message = dto.message,
        postId = dto.postId,
        type = dto.type,
        timestamp = dto.timestamp
    )
}