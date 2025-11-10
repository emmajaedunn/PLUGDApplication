package com.example.plugd.data.mappers

import com.example.plugd.data.localRoom.entity.MessageEntity
import com.example.plugd.model.Message

fun MessageEntity.toMessage(): Message = Message(
    id = messageId,
    channelId = channelId,
    senderId = senderId,
    senderName = senderName,
    content = content,
    timestamp = timestamp
)

fun Message.toMessageEntity(): MessageEntity = MessageEntity(
    messageId = id,
    channelId = channelId,
    senderId = senderId,
    senderName = senderName ?: "",
    content = content,
    timestamp = timestamp
)