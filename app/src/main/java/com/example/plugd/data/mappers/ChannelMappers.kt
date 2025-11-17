package com.example.plugd.data.mappers

import com.example.plugd.data.localRoom.entity.ChannelEntity
import com.example.plugd.model.Channel

fun ChannelEntity.toChannel(): Channel = Channel(
    id = channelId,
    name = name
)

fun Channel.toChannelEntity(): ChannelEntity = ChannelEntity(
    channelId = id,
    name = name
)